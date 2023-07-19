package cn.hhh.server.entity.vo.user;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description 接受登录参数
 * @Author HHH
 * @Date 2023/7/19 17:31
 */
@Data
public class LoginVo implements Serializable {

    @ApiModelProperty(value = "登录账号", example = "test", required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(value = "登录密码", example = "123456", required = true)
    @NotBlank(message = "密码不能为空")
    private String userPwd;

}
