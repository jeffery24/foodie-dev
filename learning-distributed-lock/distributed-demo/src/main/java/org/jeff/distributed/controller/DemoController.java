package org.jeff.distributed.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeff.distributed.dao.DistributeLockMapper;
import org.jeff.distributed.model.DistributeLock;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@Slf4j
public class DemoController {

    private Lock lock = new ReentrantLock();

    @RequestMapping("/singleLock")
    public String singleLock() throws InterruptedException {
        log.info("我进入了方法");
        lock.lock();
        log.info("我进入了锁");
        TimeUnit.SECONDS.sleep(60);
        lock.unlock();
        return "我已经执行完成！";
    }

    @Resource
    private DistributeLockMapper distributeLockExtMapper;

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/singleLock-db")
    public String singleLockDb() throws InterruptedException {
        log.info("我进入了方法");
        // 执行这个查询就相当于得到了一把分布式锁
        DistributeLock dbLock = distributeLockExtMapper.select("demo");
        log.info("我进入了锁");
        TimeUnit.SECONDS.sleep(60);
        return "我已经执行完成！";
    }
}