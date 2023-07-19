package cn.hhh.server.entity.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @Description 删除用户vo
 * @Author HHH
 * @Date 2023/7/20 5:41
 */
@Data
public class DeleteUserVo {

    @ApiModelProperty(value = "用户id列表", required = true)
    @NotNull(message = "用户id列表不能为空")
    @Size(min = 1, message = "用户id列表不能为空")
    private List<Long> list;

}
