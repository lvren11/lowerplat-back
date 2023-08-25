package com.ecnu.center.service;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.entity.Databasetablefield;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ecnu.center.param.DatatablefiledParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JYH
 * @since 2023-08-08
 */
public interface IDatabasetablefieldService extends IService<Databasetablefield> {
    List<Databasetablefield> getFiledNames(Databasesource databaseSource, String tablename, String tableId);

    List<Databasetablefield> getfiledsfromdatabase(String tableId);

    boolean addTablefield(Databasesource databasesource, DatatablefiledParam datatablefiledParam);

    boolean deleteTablefiled(Databasesource databasesource, Databasetablefield databasetablefield, String tableId, String filedId, String tablename);

    boolean updatetablefiled(Databasesource databasesource, DatatablefiledParam datatablefiledParam);
}
