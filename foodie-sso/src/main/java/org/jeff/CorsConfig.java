package org.jeff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    public CorsConfig() {

    }

    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加 cors 配置信息
        CorsConfiguration config = new CorsConfiguration();
        // TODO 后面买域名后替换
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://www.music.com:8080");
        config.addAllowedOrigin("http://www.mtv.com");
        config.addAllowedOrigin("http://www.mtv.com:8080");
        config.addAllowedOrigin("http://www.music.com");

        config.addAllowedOrigin("*");

        //  设置是否发送 cookie 信息
        config.setAllowCredentials(true);

        // 设置允许的请求方式
        config.addAllowedMethod("*");
        //config.addAllowedMethod("POST");

        // 设置允许的请求头
        config.addAllowedHeader("*");

        // 2. 为 url 添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);

        // 3. 返回重新定义好的 corsSource
        return new CorsFilter(corsSource);

    }


}
