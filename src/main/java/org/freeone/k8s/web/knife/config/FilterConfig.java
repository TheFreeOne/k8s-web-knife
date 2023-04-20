package org.freeone.k8s.web.knife.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * 过滤包含..的路径
 */
@Configuration
public class FilterConfig {

//    @Bean
//    public FilterRegistrationBean unsafePathFilter() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean<>();
//        filterRegistrationBean.setFilter(new UnsafePathFilter());
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.setName("unsafePathFilter");
//        filterRegistrationBean.setOrder(1);
//        return filterRegistrationBean;
//    }
}
