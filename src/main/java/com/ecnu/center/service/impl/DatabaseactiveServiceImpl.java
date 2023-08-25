package com.ecnu.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ecnu.center.entity.Databaseactive;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.mapper.DatabaseactiveMapper;
import com.ecnu.center.mapper.DatabasesourceMapper;
import com.ecnu.center.service.IDatabaseactiveService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecnu.center.utils.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JYH
 * @since 2023-08-07
 */
@Service
public class DatabaseactiveServiceImpl extends ServiceImpl<DatabaseactiveMapper, Databaseactive> implements IDatabaseactiveService {

    @Autowired(required = false)
    private DatabaseactiveMapper databaseactiveMapper;
    @Autowired(required = false)
    private RedisService redisService;
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean addactive(String useId, String url, String datasource_id) {
        Databaseactive databaseactive = new Databaseactive();
        databaseactive.setUserId(useId);
        databaseactive.setDatasourseId(datasource_id);
        databaseactive.setUrl(url);
        databaseactive.setIsactive(true);
        databaseactiveMapper.update(null, new LambdaUpdateWrapper<Databaseactive>().set(Databaseactive::getIsactive, false).eq(Databaseactive::getIsactive, true).eq(Databaseactive::getUserId, useId));
        int res = databaseactiveMapper.insert(databaseactive);
        if(res > 0){
            //这里还会存在一个问题  如果MySQL操作成功了，但Redis突然出现异常，操作失败，如何回滚MySQL操作
            try {
                String key = "user" + useId;
                deleteDatafromRedis(key);
            }catch (Exception e) {
                // 如果其他操作出现异常，则手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw e;
            }
        }
        return res > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update_active(String useId, String datasource_id) {
        databaseactiveMapper.update(null, new LambdaUpdateWrapper<Databaseactive>().set(Databaseactive::getIsactive, false).eq(Databaseactive::getIsactive, true).eq(Databaseactive::getUserId, useId));
        int res = databaseactiveMapper.update(null, new LambdaUpdateWrapper<Databaseactive>().set(Databaseactive::getIsactive, true).eq(Databaseactive::getDatasourseId, datasource_id));
        if(res > 0){
            try {
                String key = "user" + useId;
                deleteDatafromRedis(key);
            }catch (Exception e) {
                // 如果其他操作出现异常，则手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw e;
            }
        }
        return res > 0;
    }

    public void deleteDatafromRedis(String key) {
        // 使用RedisTemplate将数据保存到Redis
        redisService.deleteObject(key);
    }


}
