package com.ecnu.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.entity.Databasetable;
import com.ecnu.center.entity.Databasetablefield;
import com.ecnu.center.mapper.DatabasetablefieldMapper;
import com.ecnu.center.param.DatatablefiledParam;
import com.ecnu.center.service.IDatabasetablefieldService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecnu.center.utils.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JYH
 * @since 2023-08-08
 */
@Service
public class DatabasetablefieldServiceImpl extends ServiceImpl<DatabasetablefieldMapper, Databasetablefield> implements IDatabasetablefieldService {

    @Autowired(required = false)
    private DatabasetablefieldMapper databasetablefieldMapper;
    @Autowired
    private RedisService redisService;
    public List<Databasetablefield> getFiledNames(Databasesource databaseSource, String tablename, String tableId) {
        List<Databasetablefield> fields = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(databaseSource.getUrl(), databaseSource.getUsername(), databaseSource.getPassword())) {
            String currentDatabase = conn.getCatalog();
            try (Statement stmt = conn.createStatement()) {

                try (ResultSet fieldres = stmt.executeQuery("SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT, IS_NULLABLE, COLUMN_COMMENT " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + currentDatabase + "' AND TABLE_NAME = '" + tablename + "'")) {
                    while (fieldres.next()) {
                        Databasetablefield field = new Databasetablefield();
                        field.setFieldName(fieldres.getString("COLUMN_NAME"));
                        field.setFiledType(fieldres.getString("COLUMN_TYPE"));
                        field.setData(fieldres.getString("COLUMN_DEFAULT"));
                        field.setFiledNull(fieldres.getString("IS_NULLABLE"));
                        field.setComment(fieldres.getString("COLUMN_COMMENT"));
                        field.setTableId(tableId);
                        fields.add(field);
                    }
                }
              }
        } catch (SQLException e) {
            System.err.println("Failed to fetch table names: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
        return fields;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Databasetablefield> getfiledsfromdatabase(String tableId) {
        String key = "table" + tableId;
        List<Databasetablefield> databasetablefields = redisService.getCacheList(key);
        if(!redisService.hashkey(key)){
            databasetablefields = databasetablefieldMapper.selectList(new LambdaQueryWrapper<Databasetablefield>().eq(Databasetablefield::getTableId, tableId));
            if (databasetablefields != null) {
                redisService.setCacheList(key, databasetablefields);
            }
        }
        return databasetablefields;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addTablefield(Databasesource databasesource, DatatablefiledParam datatablefiledParam) {
        Databasetablefield databasetablefield = new Databasetablefield();
        String tableName = datatablefiledParam.getTablename(); // 表名
        String newFieldName = datatablefiledParam.getDatabasetablefield().getFieldName(); // 新字段名
        String newFieldType = datatablefiledParam.getDatabasetablefield().getFiledType(); // 新字段的数据类型
        String newFileNull = datatablefiledParam.getDatabasetablefield().getFiledNull().equals("YES") ? "NULL " : "NOT NULL ";
        String Default_Data = datatablefiledParam.getDatabasetablefield().getData();
        String NewComment = datatablefiledParam.getDatabasetablefield().getComment();

        databasetablefield.setFieldName(newFieldName);
        databasetablefield.setFiledType(newFieldType);
        databasetablefield.setFiledNull(datatablefiledParam.getDatabasetablefield().getFiledNull());
        databasetablefield.setComment(NewComment);
        databasetablefield.setData(Default_Data);
        databasetablefield.setTableId(datatablefiledParam.getTableId());
        save(databasetablefield);
        try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {

            StringBuilder alterTableSQL = new StringBuilder();
            alterTableSQL.append("ALTER TABLE ").append("`").append(tableName).append("`");
            alterTableSQL.append(" ADD COLUMN ").append(newFieldName).append(" ").append(newFieldType);
            alterTableSQL.append(" ").append(newFileNull);

            if (Default_Data != null && !Default_Data.isEmpty()) {
                alterTableSQL.append(" DEFAULT '").append(Default_Data).append("'");
            }
            if(NewComment != null && !NewComment.isEmpty()){
                alterTableSQL.append(" COMMENT '").append(NewComment).append("'");
            }

            // 2. 执行 ALTER TABLE 语句
            try (Statement stmt = conn.createStatement()) {
//                System.out.println(alterTableSQL.toString());
                stmt.executeUpdate(alterTableSQL.toString());
                try {
                    String key = "table" + datatablefiledParam.getTableId();
                    String key_table = "databaseSource" + databasesource.getDatasourseId();
                    deleteDatafromRedis(key);
                    deleteDatafromRedis(key_table);
                }catch (Exception e) {
                    // 如果其他操作出现异常，则手动回滚事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw e;
                }
            } catch (SQLException e) {
                System.err.println("Failed to add new field " + newFieldName + " to table " + tableName + ": " + e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
    public boolean deleteTablefiled(Databasesource databasesource, Databasetablefield databasetablefield, String tableId, String filedId, String tablename) {
        String fieldNameToDelete = databasetablefield.getFieldName();
        removeById(filedId);
        try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {
            String tableName = tablename; // 表名
            String alterTableSQL = "ALTER TABLE `" + tableName + "` DROP COLUMN " + fieldNameToDelete;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(alterTableSQL);
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
    public boolean updatetablefiled(Databasesource databasesource, DatatablefiledParam datatablefiledParam) {
        String oldfiledname = getById(datatablefiledParam.getDatabasetablefield()).getFieldName();
        updateById(datatablefiledParam.getDatabasetablefield());

        String tableName = datatablefiledParam.getTablename(); // 表名
        String newFieldName = datatablefiledParam.getDatabasetablefield().getFieldName(); // 新字段名
        String newFieldType = datatablefiledParam.getDatabasetablefield().getFiledType(); // 新字段的数据类型
        String newFileNull = datatablefiledParam.getDatabasetablefield().getFiledNull().equals("YES") ? "NULL " : "NOT NULL ";
        String Default_Data = datatablefiledParam.getDatabasetablefield().getData();
        String NewComment = datatablefiledParam.getDatabasetablefield().getComment();

        try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {
            //不修改字段名称
            if(oldfiledname.equals(newFieldName)) {
                // 1. 创建 ALTER TABLE 语句
                StringBuilder alterTableSQL = new StringBuilder("ALTER TABLE `" + tableName + "` MODIFY COLUMN " + newFieldName + " " + newFieldType);

                alterTableSQL.append(" ").append(newFileNull);

                if (Default_Data != null && !Default_Data.isEmpty()) {
                    alterTableSQL.append(" DEFAULT '").append(Default_Data).append("'");
                }
                if (NewComment != null && !NewComment.isEmpty()) {
                    alterTableSQL.append(" COMMENT '").append(NewComment).append("'");
                }

                // 2. 执行 ALTER TABLE 语句
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(alterTableSQL.toString());
                } catch (SQLException e) {
                    System.err.println("Failed to update field " + newFieldName + " in table " + tableName + ": " + e.getMessage());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return false;
                }
            }else{

                StringBuilder alterTableAddColumnSQL = new StringBuilder();
                alterTableAddColumnSQL.append("ALTER TABLE ").append(tableName);
                alterTableAddColumnSQL.append(" ADD COLUMN ").append(newFieldName).append(" ").append(newFieldType);
                alterTableAddColumnSQL.append(" ").append(newFileNull);

                if (Default_Data != null && !Default_Data.isEmpty()) {
                    alterTableAddColumnSQL.append(" DEFAULT '").append(Default_Data).append("'");
                }
                if(NewComment != null && !NewComment.isEmpty()){
                    alterTableAddColumnSQL.append(" COMMENT '").append(NewComment).append("'");
                }

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(alterTableAddColumnSQL.toString());
                    System.out.println("Added new field " + newFieldName + " to table " + tableName);
                    // 更新新字段数据
                    String updateDataSQL = "UPDATE " + tableName + " SET " + newFieldName + " = " + oldfiledname;
                    stmt.executeUpdate(updateDataSQL);
                    System.out.println("Updated data in new field " + newFieldName);
                } catch (SQLException e) {
                    System.err.println("Failed to add new field " + newFieldName + " to table " + tableName + ": " + e.getMessage());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return false;
                }

                // 2. 删除旧字段
                String alterTableDropColumnSQL = "ALTER TABLE " + tableName + " DROP COLUMN " + oldfiledname;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(alterTableDropColumnSQL);
                    System.out.println("Dropped old field " + oldfiledname + " from table " + tableName);
                } catch (SQLException e) {
                    System.err.println("Failed to drop old field " + oldfiledname + " from table " + tableName + ": " + e.getMessage());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        try {
            String key = "table" + datatablefiledParam.getTableId();
            deleteDatafromRedis(key);
        } catch (Exception e) {
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
