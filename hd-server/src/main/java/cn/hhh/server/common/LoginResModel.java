package cn.hhh.server.common;

import cn.hhh.server.constant.ResultConst;
import cn.hhh.server.entity.dto.UserDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description LoginResModel
 * @Author HHH
 * @Date 2023/7/19 17:33
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResModel extends BasicRes<UserDto> {

    public static LoginResModel error(ResultConst resultConst, String summary){
        LoginResModel model = new LoginResModel();
        model.setCode(resultConst.name());
        model.setErrorCode(resultConst.errorCode());
        model.setSummary(summary);
        return model;
    }

}
