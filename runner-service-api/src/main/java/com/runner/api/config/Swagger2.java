package com.runner.api.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    @Bean
    public Docket createRestApi() {
        Predicate<RequestHandler> userPredicate =
                RequestHandlerSelectors.basePackage("com.runner.user.controller");
        Predicate<RequestHandler> taskPredicate =
                RequestHandlerSelectors.basePackage("com.runner.task.controller");
        Predicate<RequestHandler> walletPredicate =
                RequestHandlerSelectors.basePackage("com.runner.wallet.controller");
        Predicate<RequestHandler> adminPredicate =
                RequestHandlerSelectors.basePackage("com.runner.admin.controller");
        Predicate<RequestHandler> filesPredicate =
                RequestHandlerSelectors.basePackage("com.runner.files.controller");

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(Predicates.or(userPredicate, taskPredicate, walletPredicate,
                        adminPredicate, filesPredicate))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("校园闪电侠·跑腿平台接口api")
                .contact(new Contact("runner",
                        "https://runner.gzmu.com",
                        "support@runner.gzmu.com"))
                .description("校园跑腿平台提供的api文档")
                .version("1.0.0")
                .termsOfServiceUrl("https://runner.gzmu.com")
                .build();
    }
}