package txy.learn.connection.redis;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RedisClientTest {

    static RedisClient redisClient;

    @BeforeClass
    public static void setUp() {
        String host = "127.0.0.1";
        Integer port = 6379;
        redisClient = new RedisClient(host, port);
    }

    @Test
    public void ping() {
        String ping = redisClient.ping();
        assertEquals("PONG", ping);
    }

}