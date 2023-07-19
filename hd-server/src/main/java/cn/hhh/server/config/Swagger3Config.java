package cn.hhh.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

/**
 * @Description swagger配置类
 * @Author HHH
 * @Date 2023/7/19 17:51
 */
@Configuration
@EnableOpenApi
public class Swagger3Config {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.hhh.server.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("慧得系统在线API文档")
                .description("慧得系统在线API文档")
                .version("1.0.0")
                .build();
    }

    private SecurityScheme securityScheme() {
        return new ApiKey("token", "token", "header");
    }

    private SecurityContext securityContext() {
        AuthorizationScope[] scopes = {
                new AuthorizationScope("global", "accessEverything")
        };

        SecurityReference reference = SecurityReference
                .builder()
                .reference("token")
                .scopes(scopes)
                .build();

        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(reference))
                .forPaths(PathSelectors.regex("(?!/hd/login).*")) // 排除/hd/login路径
                .build();
    }

}
