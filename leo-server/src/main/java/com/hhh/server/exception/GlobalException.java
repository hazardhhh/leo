package com.hhh.server.exception;

import com.hhh.server.pojo.RespRes;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 * @author HHH
 * @version 1.0.0
 * @date 2022/2/19 1:12
 */
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(SQLException.class)
    public RespRes mySqlException(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return RespRes.error("该数据有关联数据，操作失败！");
        }
        return RespRes.error("数据库异常，操作失败！");
    }
}
