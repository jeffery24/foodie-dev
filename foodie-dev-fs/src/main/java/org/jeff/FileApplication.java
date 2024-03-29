package org.jeff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "org.jeff.mapper")
// 扫描所有包以及所有相关的组件包, 如果扫描org.jeff.mapper会导致请求Path404 -- 暂时未知
@ComponentScan(basePackages = {"org.jeff", "org.n3r.idworker"})
public class FileApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

}
