package cn.hhh.server.entity.vo.user;

import cn.hhh.server.entity.dto.UserDto;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description 重置用户密码Vo
 * @Author HHH
 * @Date 2023/7/20 5:41
 */
@Data
public class ResetUserPwdVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "密码不能为空")
    private String userPwd;

    public UserDto toUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUserId(this.userId);
        userDto.setUserPwd(this.userPwd);
        return userDto;
    }

}
