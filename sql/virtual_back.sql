CREATE TABLE `databasesource`(
                                 `datasourse_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '数据源id',
                                 `url` varchar(255) NOT NULL COMMENT '数据源url',
                                 `username` varchar(255) NOT NULL COMMENT '数据库用户名',
                                 `password` varchar(255) NOT NULL COMMENT '数据库密码',
                                 `databasetype` varchar(255) NOT NULL COMMENT '数据库类型',
                                 `user_id` varchar(255) NOT NULL COMMENT '用户id',
                                 `createTime`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `databaseactive`(
                                 `active_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '数据源激活id',
                                 `user_id` varchar(255) NOT NULL COMMENT '用户id',
                                 `datasourse_id` varchar(255) NOT NULL COMMENT '数据源id',
                                 `url` varchar(255) NOT NULL COMMENT '数据源url',
                                 `isactive` tinyint(1) not null COMMENT '1激活 0未激活',
                                 `createTime`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `databasetable`(
                                 `table_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '数据源表信息',
                                 `table_name` varchar(255) NOT NULL COMMENT '表名称',
                                 `rowCount` int NOT NULL COMMENT '行数',
                                 `columnCount` int NOT NULL COMMENT '列数',
                                 `indexlist` varchar(255) NOT NULL COMMENT '索引信息',
                                 `datasourse_id` varchar(255) NOT NULL COMMENT '数据源id',
                                 `createTime`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `databasetablefield`(
                                `field_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '字段id',
                                `field_name` varchar(255) NOT NULL COMMENT '字段名称',
                                `filed_type` varchar(255) NOT NULL COMMENT '字段类型',
                                `data` varchar(255) COMMENT '字段数据',
                                `filed_null` varchar(255) NOT NULL default 0 COMMENT '0 不是 null  1是 null',
                                `comment` varchar(255) COMMENT '备注',
                                `table_id` varchar(255) NOT NULL COMMENT '表id',
                                `createTime`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;

CREATE TABLE `BaseCodeToFileParam`(
                                     `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '配置id',
                                     `TbNames` varchar(255) NOT NULL COMMENT '配置表名',
                                     `TbPrefix` varchar(255) NOT NULL COMMENT '表前缀',
                                     `Author` varchar(255) NOT NULL COMMENT '作者',
                                     `Isswagger` boolean Not Null COMMENT '开启swagger',
                                     `IdType` varchar(255) NOT NULL COMMENT 'idtype',
                                     `PackageName` varchar(255) NOT NULL COMMENT '包名',
                                     `EntityName` varchar(255) NOT NULL COMMENT '实体类',
                                     `MapperName` varchar(255) NOT NULL COMMENT 'mapper名',
                                     `ServiceName` varchar(255) NOT NULL COMMENT '服务名',
                                     `ServiceImplName` varchar(255) NOT NULL COMMENT '实现名',
                                     `ControllerName` varchar(255) NOT NULL COMMENT '控制器名',
                                     `IsRestController` boolean Not Null COMMENT '开启rest',
                                     `IsLombook` boolean Not Null COMMENT '开启lambook',
                                      `table_id` varchar(255) NOT NULL COMMENT '表id',
                                     `table_name` varchar(255) NOT NULL COMMENT '表名',
                                     `datasource_id` varchar(255) NOT NULL COMMENT '数据源id',
                                     `createTime`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci;