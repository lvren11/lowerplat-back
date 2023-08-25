package com.ecnu.center.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author JYH
 * @since 2023-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("databasesource")
@ApiModel(value="Databasesource对象", description="")
public class Databasesource implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据源id")
    @TableId(value = "datasourse_id", type = IdType.AUTO)
    private Integer datasourseId;

    @ApiModelProperty(value = "数据源url")
    private String url;

    @ApiModelProperty(value = "数据库用户名")
    private String username;

    @ApiModelProperty(value = "数据库密码")
    private String password;

    @ApiModelProperty(value = "数据库类型")
    private String databasetype;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)//序列化器
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)//反序列化器
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//时间格式(含有日期和时间)
    @TableField("createTime")
    private LocalDateTime createtime;

    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)//序列化器
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)//反序列化器
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//时间格式(含有日期和时间)
    @TableField("updateTime")
    private LocalDateTime updatetime;

    @ApiModelProperty(value = "是否删除")
    @TableField("isDelete")
    private Boolean isdelete;


}
