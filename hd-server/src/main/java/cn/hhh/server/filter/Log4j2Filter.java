package cn.hhh.server.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import cn.hhh.server.logger.HdLog;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

/**
 * @Description Log4j2Filter
 * @Author HHH
 * @Date 2023/7/19 22:43
 */
@Component
@WebFilter(filterName = "Log4j2Filter", urlPatterns = "/*")
public class Log4j2Filter implements Filter {

    private static final HdLog log = HdLog.getInstance();

    public static final String TID = "tid";

    static {
        System.setProperty("log4j2.isThreadContextMapInheritable", "true");
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString().replace("-", "");
            ThreadContext.put(Log4j2Filter.TID, traceId);

            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.addHeader(Log4j2Filter.TID, traceId);

            filterChain.doFilter(servletRequest, response);
        } catch (Exception e) {
            log.error(" Log4j2Filter doFilter error ", e );
            throw e;
        } finally {
            ThreadContext.clearAll();
        }
    }

}
