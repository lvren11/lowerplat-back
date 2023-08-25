package com.ecnu.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectById;
import com.ecnu.center.entity.*;
import com.ecnu.center.mapper.DatabasetableMapper;
import com.ecnu.center.param.BaseCodeToFileParam;
import com.ecnu.center.param.DatatableParam;
import com.ecnu.center.service.IBasecodetofileparamService;
import com.ecnu.center.service.IDatabasetableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecnu.center.service.IDatabasetablefieldService;
import com.ecnu.center.utils.RedisService;
import com.fasterxml.jackson.databind.ser.Serializers;
import net.bytebuddy.asm.Advice;
import net.sf.jsqlparser.schema.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.*;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JYH
 * @since 2023-08-08
 */
@Service
public class DatabasetableServiceImpl extends ServiceImpl<DatabasetableMapper, Databasetable> implements IDatabasetableService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private IDatabasetablefieldService iDatabasetablefieldService;

    @Autowired
    private IBasecodetofileparamService iBasecodetofileparamService;


    public List<Databasetable> getTableNames(Databasesource databaseSource) {
        String key = "databaseSource" + databaseSource.getDatasourseId();
        if(!redisService.hashkey(key)) {
            List<Databasetable> tableinfo = new ArrayList<>();
            try (Connection conn = DriverManager.getConnection(databaseSource.getUrl(), databaseSource.getUsername(), databaseSource.getPassword())) {
                // 获取数据库中的所有表名
                String sql = "SHOW TABLES";
                String currentDatabase = conn.getCatalog();
                DatabaseMetaData metaData = conn.getMetaData();
                try (ResultSet resultSet = conn.createStatement().executeQuery(sql)) {
                    while (resultSet.next()) {
                        Databasetable databasetable = new Databasetable();
                        String tableName = resultSet.getString(1);
                        databasetable.setTableName(tableName);
                        ResultSet rs = metaData.getTables(null, null, tableName, null);
                        if (rs.next()) {
                            // 获取表的行数和列数
                            try (Statement stmt = conn.createStatement()) {

                                try (ResultSet rowCountResult = stmt.executeQuery("SELECT COUNT(*) FROM `" + tableName + "`")) {
                                    if (rowCountResult.next()) {
                                        databasetable.setRowcount((int) rowCountResult.getLong(1));
                                    }
                                }

                                try (ResultSet columnCountResult = stmt.executeQuery("SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + currentDatabase + "'" + "AND TABLE_NAME = '" + tableName + "'")) {
                                    if (columnCountResult.next()) {
                                        databasetable.setColumncount(columnCountResult.getInt(1));
                                    }
                                }

                                // 获取表的索引信息
                                try (ResultSet indexResult = stmt.executeQuery("SHOW INDEX FROM " + currentDatabase + "." + tableName)) {
                                    StringBuilder indexes = new StringBuilder();
                                    while (indexResult.next()) {
                                        indexes.append(indexResult.getString("COLUMN_NAME") + " ");
                                    }
                                    if (indexes.length() > 0) {
                                        indexes.deleteCharAt(indexes.length() - 1);
                                    }
                                    databasetable.setIndexlist(indexes.toString());
                                }
                            }
                            databasetable.setDatasourseId(String.valueOf(databaseSource.getDatasourseId()));
                        }
                        tableinfo.add(databasetable);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Failed to fetch table names: " + e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return null;
            }
            redisService.setCacheList(key, tableinfo);
            return tableinfo;
        }
        else{
            return redisService.getCacheList(key);
        }
    }

    public List<Databasetable> getTableFromRedis(String key) {
        // 使用RedisTemplate从Redis中获取数据
        List<Databasetable> databasetableList = redisService.getCacheList(key);
        return databasetableList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertTableToBase(Databasesource databasesource, String tablenames) {
        List<Databasetable> databasetableList = getTableFromRedis("databaseSource" + databasesource.getDatasourseId());
        List<String> tablenamelist = Arrays.asList(tablenames.split(","));
        List<Databasetable> filteredList = databasetableList.stream()
                .filter(databasetable -> tablenamelist.contains(String.valueOf(databasetable.getTableName()))).toList();

        boolean result_tables = saveBatch(filteredList);
        List<Integer> insertedIds = filteredList.stream().map(Databasetable::getTableId).toList();
        List<String> tablelists = filteredList.stream().map(Databasetable::getTableName).toList();
        for(int i = 0; i < insertedIds.size(); i++){
            iDatabasetablefieldService.saveBatch(iDatabasetablefieldService.getFiledNames(databasesource, tablelists.get(i), String.valueOf(insertedIds.get(i))));
            Basecodetofileparam basecodetofileparam = new Basecodetofileparam();
            basecodetofileparam.setAuthor("");
            basecodetofileparam.setTableId(String.valueOf(insertedIds.get(i)));
            basecodetofileparam.setIsswagger(true);
            basecodetofileparam.setEntityname("entity");
            basecodetofileparam.setMappername("mapper");
            basecodetofileparam.setControllername("controller");
            basecodetofileparam.setIdtype("ASSIGN_ID");
            basecodetofileparam.setPackagename("com.ecnu.center");
            basecodetofileparam.setIslombook(true);
            basecodetofileparam.setTbnames(tablelists.get(i));
            basecodetofileparam.setTbprefix("");
            basecodetofileparam.setServicename("service");
            basecodetofileparam.setServiceimplname("impl");
            basecodetofileparam.setTableName(tablelists.get(i));
            basecodetofileparam.setIsrestcontroller(true);
            basecodetofileparam.setDatasourceId(String.valueOf(databasesource.getDatasourseId()));
            iBasecodetofileparamService.save(basecodetofileparam);
        }
        return result_tables;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addTable(Databasesource databasesource, DatatableParam datatableParam) {
        Databasetable databasetable = new Databasetable();
        databasetable.setTableName(datatableParam.getTablename());

        databasetable.setRowcount(0);
        databasetable.setColumncount(datatableParam.getDatabasetablefields().size());
        if (datatableParam.getIndexlist().isEmpty()) {
            if (datatableParam.getPrimaryKey().isEmpty()) {
                databasetable.setIndexlist(datatableParam.getIndexlist());
            } else {
                databasetable.setIndexlist(datatableParam.getPrimaryKey());
            }
        } else {
            if (datatableParam.getPrimaryKey().isEmpty()) {
                databasetable.setIndexlist(datatableParam.getIndexlist());
            } else {
                // 将索引字符串和主键字符串分别拆分成字段列表
                List<String> indexFields = Arrays.asList(datatableParam.getIndexlist().split(","));
                List<String> primaryKeyFields = Arrays.asList(datatableParam.getPrimaryKey().split(","));

                // 合并字段列表，并去除重复字段
                Set<String> combinedFields = new HashSet<>();
                combinedFields.addAll(indexFields);
                combinedFields.addAll(primaryKeyFields);

                // 将合并后的字段列表重新组装成字符串
                String combinedIndexList = String.join(",", combinedFields);

                databasetable.setIndexlist(combinedIndexList);
            }
        }
        databasetable.setDatasourseId(String.valueOf(databasesource.getDatasourseId()));
        save(databasetable);

        Basecodetofileparam basecodetofileparam = new Basecodetofileparam();
        basecodetofileparam.setAuthor("");
        basecodetofileparam.setTableId(String.valueOf(databasetable.getTableId()));
        basecodetofileparam.setIsswagger(true);
        basecodetofileparam.setEntityname("entity");
        basecodetofileparam.setMappername("mapper");
        basecodetofileparam.setControllername("controller");
        basecodetofileparam.setIdtype("ASSIGN_ID");
        basecodetofileparam.setPackagename("com.ecnu.center");
        basecodetofileparam.setIslombook(true);
        basecodetofileparam.setTbnames(datatableParam.getTablename());
        basecodetofileparam.setTbprefix("");
        basecodetofileparam.setServicename("service");
        basecodetofileparam.setServiceimplname("impl");
        basecodetofileparam.setTableName(datatableParam.getTablename());
        basecodetofileparam.setIsrestcontroller(true);
        basecodetofileparam.setDatasourceId(String.valueOf(databasesource.getDatasourseId()));
        iBasecodetofileparamService.save(basecodetofileparam);

        try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {
            // 添加一个新表
            StringBuilder createTableSQL = new StringBuilder("CREATE TABLE `" + datatableParam.getTablename() + "` (");
            String primaryKey = datatableParam.getPrimaryKey();
            String[] primaryKeyFields = primaryKey.split(",");
            // 构建字段定义部分
            for (Databasetablefield field : datatableParam.getDatabasetablefields()) {

                if (field.getFieldName().equals(primaryKey)) {
                    createTableSQL.append("`").append(field.getFieldName()).append("` ");
                    if ("int".equalsIgnoreCase(field.getFiledType())) {
                        if (datatableParam.isAutoIncrementId()) {
                            createTableSQL.append("INT AUTO_INCREMENT PRIMARY KEY ");
                        } else {
                            createTableSQL.append("INT PRIMARY KEY ");
                        }
                    } else {
                        createTableSQL.append(field.getFiledType()).append(" PRIMARY KEY ");
                    }
                } else {
                    createTableSQL.append("`").append(field.getFieldName()).append("` ")
                            .append(field.getFiledType()).append(" ");
                }
                if ("YES".equals(field.getFiledNull())) {
                    createTableSQL.append("NULL ");
                } else {
                    createTableSQL.append("NOT NULL ");
                }

                if (field.getData() != null && !field.getData().isEmpty()) {
                    createTableSQL.append("DEFAULT '").append(field.getData()).append("' ");
                }
                createTableSQL.append("COMMENT '").append(field.getComment()).append("', ");
                field.setTableId(String.valueOf(databasetable.getTableId()));
            }

            // 设置复合主键约束
            if (primaryKeyFields.length > 1) {
                StringBuilder compositePrimaryKey = new StringBuilder("PRIMARY KEY (");
                for (String primaryKeyField : primaryKeyFields) {
                    compositePrimaryKey.append("`").append(primaryKeyField.trim()).append("`, ");
                }
                compositePrimaryKey.setLength(compositePrimaryKey.length() - 2); // 去掉最后的逗号和空格
                compositePrimaryKey.append(")");

                createTableSQL.append(compositePrimaryKey);
            }

            String indexListWithout = datatableParam.getIndexlist();
            if(!indexListWithout.isEmpty()){
                createTableSQL.append("INDEX (" + indexListWithout + ")");
            }

            if (createTableSQL.toString().endsWith(", ")) {
                createTableSQL.delete(createTableSQL.length() - 2, createTableSQL.length());
            }
            createTableSQL.append(");");
            try (Statement stmt = conn.createStatement()) {
                System.out.println(createTableSQL.toString());
                stmt.executeUpdate(createTableSQL.toString());
                System.out.println("New table created: " + datatableParam.getTablename());
                boolean res = iDatabasetablefieldService.saveBatch(datatableParam.getDatabasetablefields());
                try {
                    String key = "table" + databasetable.getTableId();
                    String key_table = "databaseSource" + databasesource.getDatasourseId();
                    deleteDatafromRedis(key);
                    deleteDatafromRedis(key_table);
                }catch (Exception e) {
                    // 如果其他操作出现异常，则手动回滚事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw e;
                }
                return res;
            } catch (SQLException e) {
                System.err.println("Failed to create table: " + e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deletetable(Databasesource databasesource, String tableId) {
        String tablename = getById(tableId).getTableName();
        removeById(tableId);
        iDatabasetablefieldService.remove(new LambdaQueryWrapper<Databasetablefield>().eq(Databasetablefield::getTableId, tableId));
        iBasecodetofileparamService.remove(new LambdaQueryWrapper<Basecodetofileparam>().eq(Basecodetofileparam::getTableId,tableId));
        try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {
            String sql = "DROP TABLE `" + tablename + "`";

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
                try {
                    String key = "table" + tableId;
                    String key_table = "databaseSource" + databasesource.getDatasourseId();
                    deleteDatafromRedis(key);
                    deleteDatafromRedis(key_table);
                }catch (Exception e) {
                    // 如果其他操作出现异常，则手动回滚事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw e;
                }
            } catch (SQLException e) {
                System.err.println("Failed to delete table " + tablename + ": " + e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updatetable(Databasesource databasesource, String tableId, String newtablename) {
        Databasetable oldtable = getById(tableId);
        String oldTableName = oldtable.getTableName();
        update(new LambdaUpdateWrapper<Databasetable>().set(Databasetable::getTableName, newtablename).eq(Databasetable::getTableId, tableId));
        iBasecodetofileparamService.update(new LambdaUpdateWrapper<Basecodetofileparam>().set(Basecodetofileparam::getTableName, newtablename).set(Basecodetofileparam::getTbnames, newtablename).eq(Basecodetofileparam::getTableId, tableId));
        try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {
            String newTableName = newtablename; // 新表名

//            // 1. 创建新表
//            String createTableSQL = "CREATE TABLE " + newTableName + " LIKE " + oldTableName;
//            try (Statement stmt = conn.createStatement()) {
//                stmt.executeUpdate(createTableSQL);
//                System.out.println("New table " + newTableName + " created.");
//            } catch (SQLException e) {
//                System.err.println("Failed to create new table " + newTableName + ": " + e.getMessage());
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                return false;
//            }
//
//            // 2. 复制数据
//            String copyDataSQL = "INSERT INTO " + newTableName + " SELECT * FROM " + oldTableName;
//            try (Statement stmt = conn.createStatement()) {
//                stmt.executeUpdate(copyDataSQL);
//                System.out.println("Data copied from " + oldTableName + " to " + newTableName);
//            } catch (SQLException e) {
//                System.err.println("Failed to copy data from " + oldTableName + " to " + newTableName + ": " + e.getMessage());
//                return false;
//            }
//
//            // 3. 删除旧表
            String upTableSQL = "RENAME TABLE `" + oldTableName + "` TO `" + newTableName + "`;";
            try (Statement stmt = conn.createStatement()) {
                System.out.println(upTableSQL);
                stmt.executeUpdate(upTableSQL);
                System.out.println("RENAME table " + oldTableName + " deleted.");
            } catch (SQLException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                System.err.println("Failed to RENAME old table " + oldTableName + ": " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return false;
        }

        try {
            String key_table = "databaseSource" + databasesource.getDatasourseId();
            deleteDatafromRedis(key_table);
        }catch (Exception e) {
            // 如果其他操作出现异常，则手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return true;
    }

    public void deleteDatafromRedis(String key) {
        // 使用RedisTemplate将数据保存到Redis
        redisService.deleteObject(key);
    }
}
