package cn.hhh.server.common;

import cn.hhh.server.constant.ResultConst;

/**
 * @Description 基础响应对像
 * @Author HHH
 * @Date 2023/7/19 22:00
 */
public class BasicServiceModel<T> extends BasicRes<T> {

    public static <T> BasicServiceModel<T> ok(T data){
        BasicServiceModel<T> model = new BasicServiceModel<>();
        model.setCode(ResultConst.S_OK.name());
        model.setVar(data);
        return model;
    }

    public static <T> BasicServiceModel<T> ok(ResultConst resultConst, T data){
        BasicServiceModel<T> model = new BasicServiceModel<>();
        model.setCode(resultConst.name());
        model.setErrorCode(resultConst.errorCode());
        model.setVar(data);
        return model;
    }

    public static <T> BasicServiceModel<T> error(ResultConst resultConst, String summary){
        BasicServiceModel<T> model = new BasicServiceModel<>();
        model.setCode(resultConst.name());
        model.setErrorCode(resultConst.errorCode());
        model.setSummary(summary);
        return model;
    }

    public boolean isOk(){
        return ResultConst.haveOk(this.code);
    }

}
