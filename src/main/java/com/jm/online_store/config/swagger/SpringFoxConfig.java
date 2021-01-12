package com.jm.online_store.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        return new Docket(DocumentationType.SWAGGER_2).groupName("All")
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
    @Bean
    public Docket apiManager() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Manager")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jm.online_store.controller.rest.manager"))
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
    @Bean
    public Docket apiAdmin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Admin")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jm.online_store.controller.rest.admin"))
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
    @Bean
    public Docket apiCustomer() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Customer")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jm.online_store.controller.rest.customer"))
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
    @Bean
    public Docket apiModerator() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Moderator")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jm.online_store.controller.rest.moderator"))
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

    @Bean
    public Docket apiServiceWorker() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("ServiceWorker")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jm.online_store.controller.rest.serviceWorker"))
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
}