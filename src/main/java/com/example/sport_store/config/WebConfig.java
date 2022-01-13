package com.example.sport_store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * Конфигурация источников статических данных
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(
                        "/webjars/**",
                        "upload/product_image/**",
                        "upload/customer_image/**")
                .addResourceLocations(
                        "/webjars/",
                        "file:upload/product_image/",
                        "file:upload/customer_image/");
    }
}
