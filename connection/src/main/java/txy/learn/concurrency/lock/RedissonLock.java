package txy.learn.concurrency.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonLock {

    private final RedissonClient redisson;

    public RedissonLock(String host, int port) {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%d", host, port));
        redisson = Redisson.create(config);
    }

    public RedissonClient getRedisson() {
        return redisson;
    }
}
