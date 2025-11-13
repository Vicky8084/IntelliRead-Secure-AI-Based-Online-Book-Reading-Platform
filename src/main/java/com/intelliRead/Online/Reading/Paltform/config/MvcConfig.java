package com.intelliRead.Online.Reading.Paltform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // ✅ Direct URL mappings for HTML files in templates folder
        registry.addViewController("/User-Dashboard").setViewName("User-Dashboard");
//        registry.addViewController("/Login").setViewName("Login");
//        registry.addViewController("/Books").setViewName("Books");
//        registry.addViewController("/Home").setViewName("Home");
    }
}
