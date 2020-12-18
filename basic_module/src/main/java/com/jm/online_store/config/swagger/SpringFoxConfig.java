package com.jm.online_store.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jm.online_store.controller.rest"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(
                    new ApiInfo(
                            "Online Store",
                            "Online store - a place to buy all you need",
                            "apiVersion",
                            null,
                            null,
                            null,
                            null,
                            Collections.emptyList()
                    )
                );
    }

/*    @Bean
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("My API Title")
                .description("Awesome description")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version("1.0.0")
                .build();
    }*/

}