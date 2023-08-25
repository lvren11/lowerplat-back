package com.ecnu.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ecnu.center.common.ResponseInfo;
import com.ecnu.center.entity.Databaseactive;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.entity.Databasetable;
import com.ecnu.center.entity.Databasetablefield;
import com.ecnu.center.mapper.DatabasesourceMapper;
import com.ecnu.center.param.DatatablePageParam;
import com.ecnu.center.service.IDatabaseactiveService;
import com.ecnu.center.service.IDatabasesourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecnu.center.service.IDatabasetableService;
import com.ecnu.center.service.IDatabasetablefieldService;
import com.ecnu.center.utils.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.sql.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JYH
 * @since 2023-08-07
 */
@Service
public class DatabasesourceServiceImpl extends ServiceImpl<DatabasesourceMapper, Databasesource> implements IDatabasesourceService {

    @Autowired(required = false)
    private DatabasesourceMapper databasesourceMapper;

    @Autowired
    private IDatabaseactiveService iDatabaseactiveService;

    @Autowired
    private IDatabasetableService iDatabasetableService;

    @Autowired
    private IDatabasetablefieldService iDatabasetablefieldService;
    @Autowired(required = false)
    private RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseInfo savedatasource(Databasesource databasesource) {
        int result = databasesourceMapper.insert(databasesource);
        if(result > 0){
            try (Connection conn = DriverManager.getConnection(databasesource.getUrl(), databasesource.getUsername(), databasesource.getPassword())) {
                System.out.println("Connected to the database!");

                // Test query to check if the connection is working
                try (Statement stmt = conn.createStatement()) {
                    ResultSet resultSet = stmt.executeQuery("SELECT 1");
                    if (resultSet.next()) {
                        System.out.println("Connection test query result: " + resultSet.getInt(1));
                        //这里遇到先删除缓存还是先更新数据库？
                        boolean result_active = iDatabaseactiveService.addactive(databasesource.getUserId(), databasesource.getUrl(), String.valueOf(databasesource.getDatasourseId()));
                        if(result_active){
                            return ResponseInfo.success();
                        }else{
                            return ResponseInfo.fail("数据库激活失败");
                        }
                    }
                }
            }catch (SQLException e) {
                System.err.println("Failed to fetch table names: " + e.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseInfo.fail("连接数据库错误");
            }
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("添加失败");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Databasesource getDataFromRedis(String userId) {
        // 使用RedisTemplate从Redis中获取数据
        String key = "user" + userId;
        Databasesource databasesource = redisService.getCacheObject(key);
        if(!redisService.hashkey(key)){
            String datasource_id = iDatabaseactiveService.getOne(new LambdaQueryWrapper<Databaseactive>().eq(Databaseactive::getUserId, userId).eq(Databaseactive::getIsactive, true)).getDatasourseId();
            databasesource = databasesourceMapper.selectById(datasource_id);
            if (databasesource != null) {
                redisService.setCacheObject(key, databasesource);
            }
        }
        return databasesource;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deletedatasource(DatatablePageParam datatablePageParam) {
        String data_id = datatablePageParam.getDatasourceId();
        String key_datasource = "user" + datatablePageParam.getUserId();
        boolean res = removeById(data_id);
        iDatabaseactiveService.remove(new LambdaQueryWrapper<Databaseactive>().eq(Databaseactive::getDatasourseId, data_id));
        String key_table = "databaseSource" + datatablePageParam.getDatasourceId();
        Databasetable databasetable = iDatabasetableService.getOne(new LambdaQueryWrapper<Databasetable>().eq(Databasetable::getDatasourseId, data_id));
        String key_filed = null;
        if(databasetable != null) {
            String tableId = String.valueOf(databasetable.getTableId());
            key_filed = "table" + tableId;
            iDatabasetableService.removeById(tableId);
            iDatabasetablefieldService.remove(new LambdaQueryWrapper<Databasetablefield>().eq(Databasetablefield::getTableId, tableId));
        }
        try {
            deleteDatafromRedis(key_datasource);
            deleteDatafromRedis(key_table);
            if(databasetable != null) {
                deleteDatafromRedis(key_filed);
            }
        }catch (Exception e) {
            // 如果其他操作出现异常，则手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        return res;
    }
    public void deleteDatafromRedis(String key) {
        // 使用RedisTemplate将数据保存到Redis
        redisService.deleteObject(key);
    }
}
