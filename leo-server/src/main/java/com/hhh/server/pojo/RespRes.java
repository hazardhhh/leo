package com.hhh.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公共返回对象
 *
 * @author HHH
 * @date 2022/1/21
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespRes {

    private long code;

    private String message;

    private Object result;

    /**
     * 成功返回结果
     *
     * @param message
     * @return
     */
    public static RespRes success(String message) {
        return new RespRes(200, message, null);
    }

    /**
     * 成功返回结果
     *
     * @param message
     * @param obj
     * @return
     */
    public static RespRes success(String message, Object obj) {
        return new RespRes(200, message, obj);
    }

    /**
     * 失败返回结果
     *
     * @param message
     * @return
     */
    public static RespRes error(String message) {
        return new RespRes(500, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param message
     * @param obj
     * @return
     */
    public static RespRes error(String message, Object obj) {
        return new RespRes(500, message, obj);
    }

}
