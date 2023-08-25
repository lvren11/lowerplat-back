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
 * @since 2023-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("databaseactive")
@ApiModel(value="Databaseactive对象", description="")
public class Databaseactive implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据源激活id")
    @TableId(value = "active_id", type = IdType.AUTO)
    private Integer activeId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "数据源id")
    private String datasourseId;

    @ApiModelProperty(value = "1激活 0未激活")
    private Boolean isactive;

    @ApiModelProperty(value = "url")
    private String url;

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
