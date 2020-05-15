package com.iccgame.ssoserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.iccgame.ssoserver.mapper")
public class SsoServerApplication  extends SpringBootServletInitializer {

    //重写配置方法
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SsoServerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SsoServerApplication.class, args);
    }

}
