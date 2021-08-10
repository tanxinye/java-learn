package txy.learn.concurrency.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;

public class RedisLock {

    private final JedisPool jedisPool;

    public RedisLock(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(8);
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxWaitMillis(10000);
        jedisPool = new JedisPool(poolConfig, host, port, 6000, password, 0);
    }


    public boolean lock(String key, String value, long px, long waitTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            long deadline = System.currentTimeMillis() + waitTime;
            for (; ; ) {
                String ret = jedis.set(key, value, SetParams.setParams().nx().px(px));
                if ("OK".equals(ret)) {
                    return true;
                }

                if (deadline < System.currentTimeMillis()) {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unlock(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            Object result = jedis.eval(luaScript, Collections.singletonList(key),
                    Collections.singletonList(value));

            if ("1".equals(result)) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
