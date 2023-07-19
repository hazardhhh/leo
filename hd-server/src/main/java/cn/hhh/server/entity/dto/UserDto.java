package cn.hhh.server.entity.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hhh.server.handler.UserIdAutoFillHandler;
import com.tangzc.mpe.annotation.DefaultValue;
import com.tangzc.mpe.annotation.InsertOptionDate;
import com.tangzc.mpe.annotation.InsertOptionUser;
import com.tangzc.mpe.annotation.InsertUpdateOptionDate;
import com.tangzc.mpe.annotation.InsertUpdateOptionUser;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Description 用户信息表
 * @Author HHH
 * @Date 2023/7/19 16:58
 */
@Data
@TableName("hd_user")
@ExcelIgnoreUnannotated
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id")
    private Long userId;

    @TableField("user_name")
    @ApiModelProperty(value = "用户名")
    @NotBlank(message = "用户姓名不能为空")
    @Length(max = 20, message = "用户姓名过长")
    @ExcelProperty(value = "用户名", index = 0)
    private String userName;

    @TableField("user_pwd")
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String userPwd;

    @TableField("user_type")
    @ApiModelProperty(value = "用户类型（0管理员，1普通用户）")
    @DefaultValue("1")
    private Integer userType;

    @TableField("create_user_id")
    @ApiModelProperty(value = "创建者id")
    @InsertOptionUser(UserIdAutoFillHandler.class)
    private Integer createUserId;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    @InsertOptionDate
    @ExcelProperty(value = "创建时间", index = 1)
    private Date createTime;

    @TableField("last_modify_user_id")
    @ApiModelProperty(value = "最后修改人id")
    @InsertUpdateOptionUser(UserIdAutoFillHandler.class)
    private Integer lastModifyUserId;

    @TableField("last_modify_time")
    @ApiModelProperty(value = "最后修改时间")
    @InsertUpdateOptionDate
    private LocalDateTime lastModifyTime;

    @TableField("last_login_time")
    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @TableField(exist = false)
    @ApiModelProperty(value = "最后修改人名称")
    private String lastModifyUserName;

    @TableField(exist = false)
    private String token;

}
