package cn.hhh.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import cn.hhh.server.constant.ResultConst;
import cn.hhh.server.common.StrBasicRes;
import cn.hhh.server.logger.HdLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description 统一异常拦截
 * @Author HHH
 * @Date 2023/7/19 23:01
 */
@RestControllerAdvice
public class ControllerAdvice {

    private static final HdLog log = HdLog.getInstance();

    /**
     * 拦截JSON参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public StrBasicRes bindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String defaultMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
        StrBasicRes res = new StrBasicRes();
        res.setCode(ResultConst.PARAM_ERROR.name());
        res.setMessage(defaultMessage);
        return res;
    }

    /**
     * 拦截url param参数校验
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ConstraintViolationException.class)
    public StrBasicRes bindException(ConstraintViolationException e) {
        List<String> msgList = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
            msgList.add(constraintViolation.getMessage());
        }
        String messages = StringUtils.join(msgList.toArray(), ";");

        StrBasicRes res = new StrBasicRes();
        res.setCode(ResultConst.PARAM_ERROR.name());
        res.setMessage(messages);
        return res;
    }

    /**
     * 拦截所有异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public StrBasicRes bindException(Exception e) {
        log.error("System Exception", e);
        StrBasicRes res = new StrBasicRes();
        res.setCode(ResultConst.FS_UNKNOWN.name());
        return res;
    }

}
