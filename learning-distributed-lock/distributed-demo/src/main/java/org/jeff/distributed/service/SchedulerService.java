package org.jeff.distributed.service;

import lombok.extern.slf4j.Slf4j;
import org.jeff.distributed.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SchedulerService {
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 不用锁 5 秒发送一次
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSmsNoLock() {
        log.info("向 xxxxxxxx 发送短信");
    }

    /**
     * 5 秒发送一次
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendSms() {
        try (
                RedisLock redisLock = new RedisLock(redisTemplate, "autoSendSms", 30);
        ) {
            if (redisLock.getLock()) {
                log.info("向 xxxxxxxx 发送短信");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}