package com.mnu.BlogJpa.WebConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /upload/** 로 시작하는 경로로 요청하면
        // 실제 컴퓨터의 아래 경로에서 파일을 찾아라! 라는 설정이야.
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/Users/오준현/Documents/workspace-spring-tools-for-eclipse-4.32.2.RELEASE/exBlogProject/src/main/resources/static/upload/");
    }
}