package cn.hhh.server.handler;

import cn.hhh.server.constant.CommonConst;
import com.tangzc.mpe.annotation.handler.IOptionByAutoFillHandler;
import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Description 当前登录人id自动填充
 * @Author HHH
 * @Date 2023/7/19 17:15
 */
@Component
public class UserIdAutoFillHandler implements IOptionByAutoFillHandler<Integer> {

    /**
     * @param object 当前操作的数据对象
     * @param clazz  当前操作的数据对象的class
     * @param field  当前操作的数据对象上的字段
     * @return 当前登录用户id
     */
    @Override
    public Integer getVal(Object object, Class<?> clazz, Field field) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
        // 过滤器，token校验成功后就把用户信息放到到请求中
        final String userid = (String) request.getAttribute(CommonConst.USERID_ATTR);
        return Integer.valueOf(userid);
    }

}
