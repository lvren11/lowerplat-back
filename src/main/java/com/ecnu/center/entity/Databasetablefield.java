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
 * @since 2023-08-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("databasetablefield")
@ApiModel(value="Databasetablefield对象", description="")
public class Databasetablefield implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字段id")
    @TableId(value = "field_id", type = IdType.AUTO)
    private Integer fieldId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段类型")
    private String filedType;

    @ApiModelProperty(value = "字段数据")
    private String data;

    @ApiModelProperty(value = "0 不是 null  1是 null")
    private String filedNull;

    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "表id")
    private String tableId;

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
