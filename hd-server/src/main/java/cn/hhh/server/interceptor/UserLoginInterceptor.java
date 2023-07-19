package cn.hhh.server.interceptor;

import cn.hhh.server.constant.ResultConst;
import cn.hhh.server.common.StrBasicRes;
import cn.hhh.server.utils.UserSecurityUtils;
import com.alibaba.fastjson.JSON;
import cn.hhh.server.annotation.PassToken;
import cn.hhh.server.logger.HdLog;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @Description UserLoginInterceptor
 * @Author HHH
 * @Date 2023/7/19 16:29
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    private static final HdLog log = HdLog.getInstance();

    @Autowired
    UserSecurityUtils securityUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {
            //OPTIONS请求放行
            if ("OPTIONS".equals(request.getMethod())) {
                return true;
            }

            //如果不是映射到方法直接放行通过
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 判断是否有 passToken注解,如果有通过 不进行token校验
            if (method.isAnnotationPresent(PassToken.class)) {
                PassToken annotation = method.getAnnotation(PassToken.class);
                if (annotation.required()) {
                    return true;
                }
            }

            //校验token
            boolean check = securityUtils.verifyWebToken(request, response);
            if (check) {
                return true;
            } else {
                log.warn(" user noLogin ");

                StrBasicRes res = new StrBasicRes();
                res.setCode(ResultConst.NO_LOGIN.toString());
                returnJson(response, JSON.toJSONString(res));
                return false;
            }

        } catch (Exception e) {
            log.error("UserLoginInterceptor preHandle error", e);
        }

        return false;
    }

    private void returnJson(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        } catch (IOException e) {
            log.error("returnJson error", e);
        }
    }

}