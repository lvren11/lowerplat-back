package com.ecnu.center.service.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.ecnu.center.entity.Basecodetofileparam;
import com.ecnu.center.entity.Databasesource;
import com.ecnu.center.service.IThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.sql.Time;
import java.util.concurrent.CountDownLatch;

@Service
public class ThreadService implements IThreadService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadService.class);
    @Override
    @Async("asyncMainExecutor")
    public void codegenerator(CountDownLatch countDownLatch, String rootpath, Databasesource databasesource, Basecodetofileparam baseCodeToFileParam) {
        AutoGenerator autoGenerator = new AutoGenerator();
        //数据库相关配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUrl(databasesource.getUrl());
        dataSourceConfig.setUsername(databasesource.getUsername());
        dataSourceConfig.setPassword(databasesource.getPassword());
        autoGenerator.setDataSource(dataSourceConfig);
        //设置全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(rootpath);
        globalConfig.setOpen(false);
        globalConfig.setAuthor(baseCodeToFileParam.getAuthor());
        globalConfig.setSwagger2(baseCodeToFileParam.getIsswagger());
        globalConfig.setFileOverride(true);
        globalConfig.setMapperName("%sMapper");
        globalConfig.setServiceName("%sService");
        globalConfig.setBaseResultMap(true);
        //在xml文件中生成基础表列名
        globalConfig.setBaseColumnList(true);
        if(baseCodeToFileParam.getIdtype().equals("ASSIGN_UUID")){
            globalConfig.setIdType(IdType.ASSIGN_UUID);
        }else if(baseCodeToFileParam.getIdtype().equals("AUTO")){
            globalConfig.setIdType(IdType.AUTO);
        }else if(baseCodeToFileParam.getIdtype().equals("INPUT")){
            globalConfig.setIdType(IdType.INPUT);
        }else if(baseCodeToFileParam.getIdtype().equals("NONE")){
            globalConfig.setIdType(IdType.NONE);
        }else{
            globalConfig.setIdType(IdType.ASSIGN_ID);
        }
        autoGenerator.setGlobalConfig(globalConfig);
        //设置包名相关配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(baseCodeToFileParam.getPackagename());
        packageConfig.setEntity(baseCodeToFileParam.getEntityname());
        packageConfig.setMapper(baseCodeToFileParam.getMappername());
        packageConfig.setService(baseCodeToFileParam.getServicename());
        packageConfig.setServiceImpl(baseCodeToFileParam.getServicename() + "." + baseCodeToFileParam.getServiceimplname());
        packageConfig.setController(baseCodeToFileParam.getControllername());
        autoGenerator.setPackageInfo(packageConfig);
        //策略设置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setEntityLombokModel(baseCodeToFileParam.getIslombook());
        strategyConfig.setRestControllerStyle(baseCodeToFileParam.getIsrestcontroller());
        strategyConfig.setInclude(baseCodeToFileParam.getTbnames().split(","));
        strategyConfig.setControllerMappingHyphenStyle(true);
        strategyConfig.setTablePrefix(baseCodeToFileParam.getTbprefix());
        autoGenerator.setStrategy(strategyConfig);
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setController("/templates/controller.java");
//        templateConfig.setXml(null);
        autoGenerator.setTemplate(templateConfig);
        autoGenerator.execute();
        countDownLatch.countDown();
    }
}
