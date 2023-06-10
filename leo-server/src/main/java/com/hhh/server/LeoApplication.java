package com.hhh.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 * @author hhh
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.hhh.server.mapper")
@EnableScheduling
public class LeoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeoApplication.class, args);
    }

}
