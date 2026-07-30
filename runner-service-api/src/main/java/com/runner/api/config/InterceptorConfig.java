package com.runner.api.config;

import com.runner.api.interceptors.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public UserActiveInterceptor userActiveInterceptor() {
        return new UserActiveInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor() {
        return new AdminTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 短信发送拦截器
        registry.addInterceptor(passportInterceptor())
                .addPathPatterns("/passport/getSMSCode");

        // 用户会话拦截器
        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/user/getAccountInfo")
                .addPathPatterns("/user/updateUserInfo")
                .addPathPatterns("/task/publish")
                .addPathPatterns("/task/accept")
                .addPathPatterns("/task/complete")
                .addPathPatterns("/task/cancel")
                .addPathPatterns("/task/myPublished")
                .addPathPatterns("/task/myAccepted")
//                .addPathPatterns("/wallet/**")
                .addPathPatterns("/runner/apply")
                .addPathPatterns("/runner/getStatus")
                .addPathPatterns("/fs/uploadFace")
                .addPathPatterns("/fs/uploadSomeFiles");

        // 用户激活状态拦截器
        registry.addInterceptor(userActiveInterceptor())
//                .addPathPatterns("/task/publish")
                .addPathPatterns("/task/accept")
                .addPathPatterns("/task/complete")
                .addPathPatterns("/runner/apply");

        // 管理员会话拦截器
        registry.addInterceptor(adminTokenInterceptor())
                .addPathPatterns("/adminMng/**")
                .addPathPatterns("/admin/task/**")
                .addPathPatterns("/admin/user/**")
                .addPathPatterns("/admin/runner/**");
    }
}