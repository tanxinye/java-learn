package txy.learn.connection.redis;

import redis.clients.jedis.Jedis;

public class RedisClient {

    private final Jedis jedis;

    public RedisClient(String host, Integer port) {
        jedis = new Jedis(host, port);
    }

    public String ping() {
        return jedis.ping();
    }

}
