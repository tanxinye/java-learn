package txy.learn.concurrency.lock;


import org.junit.BeforeClass;
import org.junit.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedissonLockTest {

    private static RedissonClient redisson;

    @BeforeClass
    public static void setUp() {
        String host = "127.0.0.1";
        int port = 6379;
        RedissonLock redissonLock = new RedissonLock(host, port);
        redisson = redissonLock.getRedisson();
    }

    private void executeTask(String key, String value) {
        key = "lock:" + key;
        RLock lock = redisson.getLock(key);
        lock.lock(1, TimeUnit.MINUTES);
        try {
            System.out.println(key + ":" + value);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testMulti() throws InterruptedException {
        int initThreadActiveCount = Thread.activeCount();
        for (int i = 0; i < 10; i++) {
            int flag = i;
            new Thread(() -> executeTask("test", String.valueOf(1))).start();
        }
        while (Thread.activeCount() > initThreadActiveCount) ;
        System.out.println("finish");
    }
}