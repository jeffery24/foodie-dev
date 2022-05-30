package org.jeff.distributed.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeff.distributed.lock.zookeeper.ZkLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class ZookeeperController {

    @RequestMapping("zkLock")
    public String zookeeperLock(){
        log.info("我进入了方法！");
        try (ZkLock zkLock = new ZkLock("127.0.0.1:2181", 10000)) {
            if (zkLock.getLock("order")){
                log.info("我获得了锁");
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法执行完成！");
        return "方法执行完成！";
    }


}
