package com.ecnu.center.service;

import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.entity.Databasetable;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ecnu.center.entity.Databasetablefield;
import com.ecnu.center.param.DatatableParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author JYH
 * @since 2023-08-08
 */
public interface IDatabasetableService extends IService<Databasetable> {
    List<Databasetable> getTableNames(Databasesource databaseSource);

    boolean insertTableToBase(Databasesource databasesource, String tablenames);

    boolean addTable(Databasesource databasesource, DatatableParam datatableParam);

    boolean deletetable(Databasesource databasesource, String tableId);

    boolean updatetable(Databasesource databasesource, String tableId, String newtablename);
}
