package com.example.webproject_maru.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**")

                //------배포시 수정부분------
                //로컬테스트 주소
                //.addResourceLocations("file:src/main/resources/static/");
                //배포 기준 주소
                //.addResourceLocations("file:/usr/local/tomcat/webapps/");
                .addResourceLocations("file:/app/");

        // registry.addResourceHandler("/**")
        //         .addResourceLocations("file:src/main/resources/templates/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converter.setWriteAcceptCharset(false); // Accept-Charset 응답 헤더를 제한
        converters.add(converter);
    }
}

