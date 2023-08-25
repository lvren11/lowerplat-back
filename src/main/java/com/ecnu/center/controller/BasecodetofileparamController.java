package com.ecnu.center.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.entity.Databasetable;
import com.ecnu.center.param.BasePageResult;
import com.ecnu.center.param.DatatablePageParam;
import com.ecnu.center.service.IDatabasesourceService;
import com.ecnu.center.service.IDatabasetableService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.api.R;

import com.ecnu.center.common.ResponseInfo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import com.ecnu.center.service.IBasecodetofileparamService;
import com.ecnu.center.entity.Basecodetofileparam;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JYH
 * @since 2023-08-17
 */
@RestController
@RequestMapping("/basecodetofileparam")

public class BasecodetofileparamController {

    @Autowired
    private IBasecodetofileparamService basecodetofileparamService;

    @Autowired
    private IDatabasesourceService iDatabasesourceService;

    @ApiOperation(value = "查询所有")
    @GetMapping
    public ResponseInfo findAll() {
        return ResponseInfo.success(basecodetofileparamService.list());
    }

    @ApiOperation(value = "获取某个数据源表格等信息-分页")
    @PostMapping("/gettableconfig")
    public ResponseInfo<BasePageResult<Basecodetofileparam>> gettable(@RequestBody DatatablePageParam datatablePageParam){
        Databasesource databasesource = iDatabasesourceService.getDataFromRedis(datatablePageParam.getUserId());
        IPage<Basecodetofileparam> iPage = new Page<>(datatablePageParam.getPagenum(), datatablePageParam.getPagesize());
        IPage<Basecodetofileparam> PageResult = basecodetofileparamService.page(iPage, new LambdaQueryWrapper<Basecodetofileparam>().eq(Basecodetofileparam::getDatasourceId, databasesource.getDatasourseId()));
        return ResponseInfo.success(BasePageResult.newInstance(PageResult));
    }

    @ApiOperation(value = "新增数据")
    @PostMapping("/saveconfig")
    public ResponseInfo save(@RequestBody Basecodetofileparam basecodetofileparam){
        return ResponseInfo.success(basecodetofileparamService.saveOrUpdate(basecodetofileparam));
    }

    @ApiOperation(value = "修改数据")
    @PutMapping
    public ResponseInfo update(@RequestBody Basecodetofileparam basecodetofileparam) {
        return ResponseInfo.success(basecodetofileparamService.updateById(basecodetofileparam));
    }

    @ApiOperation(value = "删除数据")
    @DeleteMapping("/{id}")
    public ResponseInfo delete(@PathVariable Integer id) {//这里的“id”必须和DeleteMapping里面的名字一样
        return ResponseInfo.success(basecodetofileparamService.removeById(id));
    }

    @ApiOperation(value = "批量删除数据")
    @PostMapping("/batchdelete")
    public ResponseInfo deleteBatch(@RequestBody List<Integer> ids) {
        return ResponseInfo.success(basecodetofileparamService.removeByIds(ids));
    }

    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public ResponseInfo findPage(
        @RequestParam Integer pageNum,
        @RequestParam Integer pageSize) {
            IPage<Basecodetofileparam> page = new Page<>(pageNum, pageSize);
            QueryWrapper<Basecodetofileparam> queryWrapper = new QueryWrapper<>();
            return ResponseInfo.success(basecodetofileparamService.page(page, queryWrapper));
    }
}


