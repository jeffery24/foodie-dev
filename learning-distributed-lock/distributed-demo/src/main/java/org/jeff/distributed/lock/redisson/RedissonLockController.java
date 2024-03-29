package org.jeff.distributed.lock.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class RedissonLockController {
    @Resource
    private RedissonClient redissonClient;

    @RequestMapping("/redisson-lock")
    public String redisLock3() {
        log.info("我进入了方法");
        final String key = "redisKey";

        RLock lock = redissonClient.getLock(key);
        // 锁超时时间，如果获取不到会阻塞等待获取到锁
        // 可以传递 -1，那么就意味着该锁没有超时时间
        lock.lock(30, TimeUnit.SECONDS);
        log.info("获得了锁");
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info("释放了锁");
            lock.unlock();
        }

        log.info("方法完成");
        return "ok";
    }
}