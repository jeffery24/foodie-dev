package org.jeff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger {
//    http://localhost:8088/swagger-ui.html     bootstrap ui路径
    // http://localhsot:8088/doc.html 原路径
    // 配置swagger2核心配置
    @Bean
    public Docket getDocket(Environment environment) {
        return new Docket(DocumentationType.SWAGGER_2)  //指定api类型为swagger2
                .apiInfo(setInfo()) //用户定义api文档汇总信息
                .select()/// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.basePackage("org.jeff.controller"))   //指定扫描controller包
                .paths(PathSelectors.any()) // 所有的controller
                .build();
    }

    public ApiInfo setInfo() {
        return new ApiInfoBuilder()
                .title("天天吃货 电商平台接口api")    // 文档页标题
                .contact(new Contact("jeff", //姓名
                        "https://jeff.org",
                        "3100611529@qq.com")) // 联系人方式邮箱
                .description("专为天天吃货提供的api文档") //详细信息
                .version("1.0.1") // 版本
                .termsOfServiceUrl("https://www.jeff.org") //网站地址
                .build();
    }


}
