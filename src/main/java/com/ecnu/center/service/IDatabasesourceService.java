package com.ecnu.center.service;

import com.ecnu.center.common.ResponseInfo;
import com.ecnu.center.entity.Databasesource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ecnu.center.param.DatatablePageParam;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JYH
 * @since 2023-08-07
 */
public interface IDatabasesourceService extends IService<Databasesource> {
    ResponseInfo savedatasource(Databasesource databasesource);
    Databasesource getDataFromRedis(String key);

    boolean deletedatasource(DatatablePageParam datatablePageParam);
}
