package org.jeff.distributed.redisnx;

import lombok.extern.slf4j.Slf4j;
import org.jeff.distributed.lock.RedisLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class RedisLockController {
    @Resource
    private RedisTemplate redisTemplate;


    @RequestMapping("/redisLock")
    public String redisLock() {
        log.info("我进入了方法");
        final String key = "redisKey";
        try (RedisLock redisLock = new RedisLock(redisTemplate, key, 30)) {
            // 获取分布式锁
            // 如果返回结果是 true ,则表示获取到了锁
            boolean lock = redisLock.getLock();
            if (lock){
                log.info("我进入了锁");
                // 模拟业务执行
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("方法完成");
        return "ok";
    }
}