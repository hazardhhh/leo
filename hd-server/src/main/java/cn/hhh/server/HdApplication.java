package cn.hhh.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * 启动类
 *
 * @author hhh
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("cn.hhh.server.mapper")
@EnableScheduling
@EnableOpenApi
public class HdApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdApplication.class, args);
    }

}
