package com.ecnu.center.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecnu.center.common.ResponseInfo;
import com.ecnu.center.entity.*;
import com.ecnu.center.param.*;
import com.ecnu.center.service.IDatabaseactiveService;
import com.ecnu.center.service.IDatabasesourceService;
import com.ecnu.center.service.IDatabasetableService;
import com.ecnu.center.service.IDatabasetablefieldService;
import com.ecnu.center.utils.RedisService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JYH
 * @since 2023-08-07
 */
@RestController
@RequestMapping("/databasesource")
public class DatabasesourceController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private IDatabasesourceService iDatabasesourceService;
    @Autowired
    private IDatabaseactiveService iDatabaseactiveService;
    @Autowired
    private IDatabasetableService iDatabasetableService;

    @Autowired
    private IDatabasetablefieldService iDatabasetablefieldService;

    @ApiOperation(value = "添加数据源")
    @PostMapping("/adddatasource")
    public ResponseInfo adddatabase(@RequestBody Databasesource databasesource){
//        System.out.println(1);
        return iDatabasesourceService.savedatasource(databasesource);
    }

    @ApiOperation(value = "删除数据源")
    @PostMapping("/deletedatasource")
    public ResponseInfo deletedatasource(@RequestBody Databasesource databasesource){
//        System.out.println(1);
        DatatablePageParam datatablePageParam = new DatatablePageParam();
        datatablePageParam.setUserId(databasesource.getUserId());
        datatablePageParam.setDatasourceId(String.valueOf(databasesource.getDatasourseId()));
        boolean res = iDatabasesourceService.deletedatasource(datatablePageParam);
        if(res){
            return ResponseInfo.success();
        }else {
            return ResponseInfo.fail("删除数据源失败");
        }
    }

    @ApiOperation(value = "刷新表格")
    @PostMapping("/refreshdatabase")
    public ResponseInfo refreshdatabase(@RequestParam(value = "userId") String userId){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(userId);
        try{
            String key = "databaseSource" + databasesource.getDatasourseId();
            redisService.deleteObject(key);
        }catch (Exception e) {
            return ResponseInfo.fail("刷新失败");
        }
        return ResponseInfo.success();
    }

    @ApiOperation(value = "查找数据源-分页")
    @PostMapping("/getdatasource")
    public ResponseInfo<BasePageResult<Databasesource>> getdatasource(@RequestBody DatasourcePageParam datasourcePageParam){
        IPage<Databasesource> iPage = new Page<>(datasourcePageParam.getPagenum(),datasourcePageParam.getPagesize());
        IPage<Databasesource> PageResult = iDatabasesourceService.page(iPage, new LambdaQueryWrapper<Databasesource>().eq(Databasesource::getUserId, datasourcePageParam.getUserId()));
        return ResponseInfo.success(BasePageResult.newInstance(PageResult));
    }

    @ApiOperation(value = "切换并激活数据源")
    @PutMapping("/putdataactive")
    public ResponseInfo putdatasouceactive(@RequestParam("userid") String userId,
                                           @RequestParam("datasourceid") String datasourceId){
        boolean res = iDatabaseactiveService.update_active(userId, datasourceId);
        if(res){
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("更新数据源状态失败");
        }
    }

    @ApiOperation(value = "获取数据源激活信息")
    @PostMapping("/getdataactive")
    public ResponseInfo<BasePageResult<Databaseactive>> getdatasourceactive(@RequestBody DatasourcePageParam datasourcePageParam){
        IPage<Databaseactive> iPage = new Page<>(datasourcePageParam.getPagenum(),datasourcePageParam.getPagesize());
        IPage<Databaseactive> PageResult = iDatabaseactiveService.page(iPage, new LambdaQueryWrapper<Databaseactive>().eq(Databaseactive::getUserId, datasourcePageParam.getUserId()));
        return ResponseInfo.success(BasePageResult.newInstance(PageResult));
    }


    @ApiOperation(value = "获取单个数据源信息")
    @GetMapping("/getonedataactive/{id}")
    public ResponseInfo<Databaseactive> getonedataactive(@PathVariable(value = "id") String userId){
        Databaseactive databaseactive = iDatabaseactiveService.getOne(new LambdaQueryWrapper<Databaseactive>().eq(Databaseactive::getUserId, userId).eq(Databaseactive::getIsactive, true));
        return ResponseInfo.success(databaseactive);
    }

    @ApiOperation(value = "获取全部表格")
    @PostMapping("/getalltable")
    public ResponseInfo<List<Databasetable>> getalltable(@RequestBody DatatablePageParam datatablePageParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(datatablePageParam.getUserId());
        return ResponseInfo.success(iDatabasetableService.getTableNames(databasesource));
    }

    @ApiOperation(value = "导入表格到数据库里")
    @PostMapping("/posttableId")
    public ResponseInfo posttable(@RequestParam(value = "tablenames") String tablenames, @RequestParam(value = "userId") String userId){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(userId);
        List<String> tablenamelist = Arrays.asList(tablenames.split(","));
        List<Databasetable> table = iDatabasetableService.list(new LambdaQueryWrapper<Databasetable>().eq(Databasetable::getDatasourseId, databasesource.getDatasourseId()));

        List<String> tablenamefromdata = table.stream()
                .map(Databasetable::getTableName).toList();
        if(tablenamefromdata.containsAll(tablenamelist)){
            return ResponseInfo.fail("导入重复表格");
        }
        boolean res = iDatabasetableService.insertTableToBase(databasesource, tablenames);
        if(res){
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("导入表格失败");
        }
    }

    @ApiOperation(value = "获取某个数据源表格等信息-分页")
    @PostMapping("/gettableinfo")
    public ResponseInfo<BasePageResult<Databasetable>> gettable(@RequestBody DatatablePageParam datatablePageParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(datatablePageParam.getUserId());
        IPage<Databasetable> iPage = new Page<>(datatablePageParam.getPagenum(), datatablePageParam.getPagesize());
        IPage<Databasetable> PageResult = iDatabasetableService.page(iPage, new LambdaQueryWrapper<Databasetable>().eq(Databasetable::getDatasourseId, databasesource.getDatasourseId()));
        return ResponseInfo.success(BasePageResult.newInstance(PageResult));
    }

    @ApiOperation(value = "打开某个数据表格等信息")
    @GetMapping("/getdetailfiled/{tableId}")
    public ResponseInfo<List<Databasetablefield>> getdetailfiled(@PathVariable String tableId){
        List<Databasetablefield> databasetablefields = iDatabasetablefieldService.getfiledsfromdatabase(tableId);
        return ResponseInfo.success(databasetablefields);
    }

    @ApiOperation(value = "增加一个表(表字段)")
    @PostMapping("/addtable")
    public ResponseInfo addtable(@RequestBody DatatableParam datatableParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(datatableParam.getUserId());
        String key = "databaseSource" + databasesource.getDatasourseId();
        if(redisService.hashkey(key)){
            List<Databasetable> databasetableList = redisService.getCacheList(key);
            List<String> tablenamefromdata = databasetableList.stream()
                    .map(Databasetable::getTableName).toList();
            if(tablenamefromdata.contains(datatableParam.getTablename())){
                return ResponseInfo.fail("数据库已经存在该表名");
            }
        }
        boolean res = iDatabasetableService.addTable(databasesource,datatableParam);
        if(res){
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("增加失败");
        }
    }

    @ApiOperation(value = "删除一个表")
    @PostMapping("/deletetable")
    public  ResponseInfo deletetable(@RequestParam("userId") String userId, @RequestParam("tableId") String tableId){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(userId);
        boolean res = iDatabasetableService.deletetable(databasesource, tableId);
        if(res){
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("删除失败");
        }
    }
    @ApiOperation(value = "修改一个表")
    @PutMapping("/puttable")
    public ResponseInfo puttable(@RequestParam("userId") String userId, @RequestParam("tableId") String tableId, @RequestParam("newtablename") String newtablename){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(userId);
        boolean res = iDatabasetableService.updatetable(databasesource, tableId, newtablename);
        if(res){
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("修改失败");
        }
    }
//
    @ApiOperation(value = "增加一个表字段")
    @PostMapping("/posttablefield")
    public ResponseInfo posttablefileld(@RequestBody DatatablefiledParam datatablefiledParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(datatablefiledParam.getUserId());
        boolean res = iDatabasetablefieldService.addTablefield(databasesource,datatablefiledParam);
        if(res){
            boolean res2 = iDatabasetableService.update(
                    new UpdateWrapper<Databasetable>()
                            .setSql("columncount = columncount + 1")
                            .eq("table_id", datatablefiledParam.getTableId()));
            if(res2) {
                return ResponseInfo.success();
            }else{
                return ResponseInfo.fail("增加失败");
            }
        }else{
            return ResponseInfo.fail("增加失败");
        }
    }

    @ApiOperation(value = "删除一个表字段")
    @PostMapping("/deletetablefiled")
    public ResponseInfo deletetablefiled(@RequestParam("userId") String userId, @RequestParam("filedId") String filedId, @RequestParam("tablename") String tablename){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(userId);
        Databasetablefield databasetablefield = iDatabasetablefieldService.getById(filedId);
        String tableId = databasetablefield.getTableId();
        boolean res = iDatabasetablefieldService.deleteTablefiled(databasesource, databasetablefield, tableId, filedId, tablename);
        if(res){
            boolean res2 = iDatabasetableService.update(
                    new UpdateWrapper<Databasetable>()
                            .setSql("columncount = columncount - 1")
                            .eq("table_id", tableId));
            if(res2) {
                return ResponseInfo.success();
            }else{
                return ResponseInfo.fail("删除失败");
            }
        }else{
            return ResponseInfo.fail("删除失败");
        }
    }
    @ApiOperation(value = "修改一个表字段")
    @PostMapping("/puttablefiled")
    public ResponseInfo puttablefiled(@RequestBody DatatablefiledParam datatablefiledParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(datatablefiledParam.getUserId());
        boolean res = iDatabasetablefieldService.updatetablefiled(databasesource, datatablefiledParam);
        if(res){
            return ResponseInfo.success();
        }else{
            return ResponseInfo.fail("修改失败");
        }
    }
//    @ApiOperation(value = "查表数据")
//    @ApiOperation(value = "增一条表数据")
//    @ApiOperation(value = "改一条表数据")
//    @ApiOperation(value = "删一条表数据")
}

