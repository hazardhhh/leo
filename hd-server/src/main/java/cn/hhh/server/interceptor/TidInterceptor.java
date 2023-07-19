package cn.hhh.server.interceptor;

import cn.hhh.server.logger.HdLog;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 打印tid拦截器，方便日志前后端排查问题
 * @Author HHH
 * @Date 2023/7/19 21:06
 */
public class TidInterceptor implements HandlerInterceptor {

    private static final HdLog log = HdLog.getInstance();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        try {
            String queryString = request.getQueryString();
            log.info(queryString);
        } catch (Exception e) {
            log.error(" TidInterceptor preHandle error ", e);
        }

        return true;
    }

}
