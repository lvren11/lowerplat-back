package com.ecnu.center.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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
 * @since 2023-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("basecodetofileparam")
@ApiModel(value="Basecodetofileparam对象", description="")
public class Basecodetofileparam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "配置表名")
    @TableField("TbNames")
    private String tbnames;

    @ApiModelProperty(value = "表前缀")
    @TableField("TbPrefix")
    private String tbprefix;

    @ApiModelProperty(value = "作者")
    @TableField("Author")
    private String author;

    @ApiModelProperty(value = "开启swagger")
    @TableField("Isswagger")
    private Boolean isswagger;

    @ApiModelProperty(value = "idtype")
    @TableField("IdType")
    private String idtype;

    @ApiModelProperty(value = "包名")
    @TableField("PackageName")
    private String packagename;

    @ApiModelProperty(value = "实体类")
    @TableField("EntityName")
    private String entityname;

    @ApiModelProperty(value = "mapper名")
    @TableField("MapperName")
    private String mappername;

    @ApiModelProperty(value = "服务名")
    @TableField("ServiceName")
    private String servicename;

    @ApiModelProperty(value = "实现名")
    @TableField("ServiceImplName")
    private String serviceimplname;

    @ApiModelProperty(value = "控制器名")
    @TableField("ControllerName")
    private String controllername;

    @ApiModelProperty(value = "开启rest")
    @TableField("IsRestController")
    private Boolean isrestcontroller;

    @ApiModelProperty(value = "开启lambook")
    @TableField("IsLombook")
    private Boolean islombook;

    @ApiModelProperty(value = "表id")
    private String tableId;

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "数据源id")
    private String datasourceId;

    @ApiModelProperty(value = "创建时间")
    @TableField("createTime")
    private LocalDateTime createtime;

    @ApiModelProperty(value = "更新时间")
    @TableField("updateTime")
    private LocalDateTime updatetime;

    @ApiModelProperty(value = "是否删除")
    @TableField("isDelete")
    private Boolean isdelete;


}
