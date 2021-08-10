package txy.learn.concurrency.lock;


import org.junit.BeforeClass;
import org.junit.Test;

public class RedisLockTest {

    static RedisLock redisLock;

    @BeforeClass
    public static void setUp() {
        String host = "127.0.0.1";
        int port = 6379;
        redisLock = new RedisLock(host, port, null);
    }

    private void executeTask(String key, String value) {
        key = "lock:" + key;
        boolean isLock = redisLock.lock(key, value, 1000, 3000);
        System.out.println(isLock);
        if (!isLock) {
            return;
        }
        try {
            System.out.println(key + ":" + value);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redisLock.unlock(key, value);
        }
    }

    @Test
    public void testSingle() {
        String key = "test";
        String value = "1";
        executeTask(key, value);
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