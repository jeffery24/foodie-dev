package org.jeff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    public CorsConfig() {

    }

    /**
     * 过滤器方式跨域配置
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加 cors 配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:8080/foodie-shop");
        config.addAllowedOrigin("http://localhost:8080/foodie-center");
        config.addAllowedOrigin("http://47.107.54.10:8080/foodie-shop");
        config.addAllowedOrigin("http://47.107.54.10:8080/foodie-center");
        config.addAllowedOrigin("http://47.107.54.10:8080");
        config.addAllowedOrigin("http://shop.z.zsj.zone/foodie-shop");
        config.addAllowedOrigin("http://shop.z.zsj.zone");
        config.addAllowedOrigin("http://center.z.zsj.zone");
        config.addAllowedOrigin("http://center.z.zsj.zone/foodie-center");
        config.addAllowedOrigin("*");

        //  设置是否发送 cookie 信息
        config.setAllowCredentials(true);

        // 设置允许的请求方式
        config.addAllowedMethod("*");
        //config.addAllowedMethod("POST");

        // 设置允许的请求头
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);//支持安全证书。跨域携带cookie需要配置这个
        //config.setMaxAge(3600L);//预检请求的有效期，单位为秒。设置maxage，可以避免每次都发出预检请

        // 2. 为 url 添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);

        // 3. 返回重新定义好的 corsSource
        return new CorsFilter(corsSource);

    }


}
