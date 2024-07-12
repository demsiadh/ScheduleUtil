package love.jiahao.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <big>线程池队列</big>
 * <p></p>
 *
 * @author 13684
 * @data 2024/7/8 下午7:08
 */
@Component
public class ScheduleQueue<E> extends ArrayBlockingQueue<E> {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private Class<E> type;
    private final ReentrantLock lock = new ReentrantLock();

    public ScheduleQueue() {
        super(500);
    }

    public ScheduleQueue(int capacity) {
        super(capacity);
    }

    public ScheduleQueue(int capacity, Class<E> type) {
        super(capacity);
        this.type = type;
    }

    /**
     * 从Redis的"schedule:rejected"列表中尝试获取一个元素，如果列表为空则从队列里获取
     * 这个方法主要用于处理被拒绝的任务，尝试从一个特定的Redis列表中获取这些任务并重新执行。
     *
     * @return 队列中的元素，或者是Redis列表中的一个被拒绝的任务。如果都为空，则根据父类的实现进行处理。
     * @throws InterruptedException 如果线程在等待锁时被中断。
     */
    @Override
    public E take() throws InterruptedException {
        // 使用ReentrantLock的中断响应能力来获取锁
        final ReentrantLock reentrantLock = this.lock;
        reentrantLock.lockInterruptibly();
        try {
            // 尝试从Redis的列表中获取一个被拒绝的任务
            String s = redisTemplate.opsForList().leftPop("schedule:rejected");
            // 如果有被拒绝的任务，则反序列化并返回
            if (StrUtil.isNotEmpty(s)) {
                return SerializeUtil.deserialize(s, type);
            }
            // 如果没有被拒绝的任务，则按照父类的逻辑处理
            return super.take();
        } finally {
            // 无论成功与否，都释放锁
            reentrantLock.unlock();
        }
    }

}
