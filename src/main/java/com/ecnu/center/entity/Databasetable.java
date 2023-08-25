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
@TableName("databasetable")
@ApiModel(value="Databasetable对象", description="")
public class Databasetable implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据源表信息")
    @TableId(value = "table_id", type = IdType.AUTO)
    private Integer tableId;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "行数")
    @TableField("rowCount")
    private Integer rowcount;

    @ApiModelProperty(value = "列数")
    @TableField("columnCount")
    private Integer columncount;

    @ApiModelProperty(value = "索引信息")
    private String indexlist;

    @ApiModelProperty(value = "数据源id")
    private String datasourseId;

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
