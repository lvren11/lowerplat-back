package com.ecnu.center.service;

import com.ecnu.center.entity.Databaseactive;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ecnu.center.entity.Databasesource;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JYH
 * @since 2023-08-07
 */
public interface IDatabaseactiveService extends IService<Databaseactive> {
    boolean addactive(String useId, String url, String datasource_id);
    boolean update_active(String useId, String datasource_id);
}
