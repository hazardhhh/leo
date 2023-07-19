package cn.hhh.server.page;

import cn.hhh.server.page.HdReqPage;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description UserReqPage
 * @Author HHH
 * @Date 2023/7/20 5:52
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserReqPage<T> extends HdReqPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名称")
    private String userName;

}
