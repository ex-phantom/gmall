package com.shadow.gmall.conf;

import com.shadow.gmall.interceptors.SSOinterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorsConf extends WebMvcConfigurerAdapter{

    @Autowired
    private SSOinterceptor ssOinterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ssOinterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
