package love.jiahao.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <big>自定义拒绝策略</big>
 * <p></p>
 *
 * @author 13684
 * @data 2024/7/8 下午5:00
 */
@Component
@Slf4j
public class ScheduleRejected implements RejectedExecutionHandler {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            redisTemplate.opsForList().leftPush("schedule:rejected", SerializeUtil.serialize(r));
        } catch (Exception e) {
            log.error("ScheduleRejected rejectedExecution error:{}", e.getMessage(), e);
        }
    }
}
