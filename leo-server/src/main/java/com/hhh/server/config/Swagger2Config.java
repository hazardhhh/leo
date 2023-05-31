package com.hhh.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger2配置类
 *
 * @author HHH
 * @date 2022/1/21 @Version 1.0.0
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Value("${ths.swaggerUrl}")
    private String swaggerUrl;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 为当前包下的controller生成api文档
                .apis(RequestHandlerSelectors.basePackage("com.hhh.server.controller"))
                .paths(PathSelectors.any())
                .build()
                // 添加登录认证
                .securityContexts(securityContexts())
                .securitySchemes(securitySchemes());
    }

    private List<SecurityContext> securityContexts() {
        // 设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/hello/.*"));
        return result;
    }

    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization", authorizationScopes));
        return result;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档")
                .description("接口文档")
//                .contact(new Contact("hhh", "http:localhost:7896/doc.html", "hhh@hhh.com"))
                .contact(new Contact("hhh", swaggerUrl, "hhh@hhh.com"))
                .version("1.0.0")
                .build();
    }

    private List<ApiKey> securitySchemes() {
        // 设置请求头信息
        List<ApiKey> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        result.add(apiKey);
        return result;
    }
}
