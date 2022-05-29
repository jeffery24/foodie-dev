package org.jeff.distributed;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.jeff.distributed.dao")
public class LearningDistributedLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningDistributedLockApplication.class, args);
    }

}
