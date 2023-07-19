package cn.hhh.server.config;

import cn.hhh.server.constant.CommonConst;
import cn.hhh.server.interceptor.UserLoginInterceptor;
import cn.hhh.server.interceptor.TidInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description webmvc配置类
 * @Author HHH
 * @Date 2023/7/19 16:23
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    UserLoginInterceptor userLoginInterceptor;

    /**
     * 跨域配置
     *
     * @param registry
     * @return
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//项目中的所有接口都支持跨域
                .allowedOriginPatterns("*")//所有地址都可以访问，也可以配置具体地址
                .allowCredentials(true)
                .exposedHeaders(CommonConst.TOKEN_HEADER)
                .allowedHeaders("*")
                .allowedMethods("*")//"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"
                .maxAge(3600);// 跨域允许时间
    }

    /**
     * 拦截器配置
     *
     * @param registry
     * @return
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 排除 swagger 访问的路径配置
        String[] swaggerExcludes = new String[]{
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/v3/**",
                "/doc.html",
        };

        //打印tid拦截器
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new TidInterceptor());
        interceptorRegistration.addPathPatterns("/**");

        //登录拦截器
        InterceptorRegistration registration = registry.addInterceptor(userLoginInterceptor);
        registration.addPathPatterns("/**");
        registration.excludePathPatterns(
                "/**/login",
                "/**/captcha/*"
        );
        registration.excludePathPatterns(swaggerExcludes);

    }

}
