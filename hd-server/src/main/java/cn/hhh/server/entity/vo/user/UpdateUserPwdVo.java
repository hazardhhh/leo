package cn.hhh.server.entity.vo.user;

import cn.hhh.server.entity.dto.UserDto;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description 更新用户密码Vo
 * @Author HHH
 * @Date 2023/7/20 5:43
 */
@Data
public class UpdateUserPwdVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    @ApiModelProperty(value = "原密码")
    @NotEmpty(message = "原密码不能为空")
    private String oldPwd;

    @ApiModelProperty(value = "新密码")
    @NotEmpty(message = "新密码不能为空")
    private String userPwd;

    public UserDto toUserDto() {
        UserDto userDto = new UserDto();
        userDto.setUserId(this.userId);
        userDto.setUserPwd(this.userPwd);
        return userDto;
    }

}
