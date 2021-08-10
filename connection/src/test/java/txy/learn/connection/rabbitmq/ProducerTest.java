package txy.learn.connection.rabbitmq;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ProducerTest {

    static Producer producer;

    @BeforeClass
    public static void setUp() {
        producer = new Producer("127.0.0.1", 5672, "xinye", "xinye", "/");
    }

    @Test
    public void conn() {
        try {
            producer.newConnection();
            assert true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void publishSimple() {
        String queue = "test";
        byte[] body = "Hello".getBytes();
        try {
            producer.publish(queue, body);
            assert true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void publishWorker() {
        String queue = "test";
        try {
            producer.publish(queue, "Hello".getBytes());
            producer.publish(queue, "Hi".getBytes());
            producer.publish(queue, "How".getBytes());
            producer.publish(queue, "Good".getBytes());
            assert true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void publishPubSub() {
        String exchange = "test-exchange";
        try {
            producer.publishFanOut(exchange, "Hello".getBytes());
            assert true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void publishRouting() {
        String exchange = "test-routing";
        try {
            producer.publishDirect(exchange, "blue", "Hello".getBytes());
            producer.publishDirect(exchange, "white", "Hi".getBytes());
            producer.publishDirect(exchange, "black", "How".getBytes());
            assert true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void publishTopic() {
        String exchange = "test-topic";
        try {
            producer.publishTopic(exchange, "color.blue", "Hello".getBytes());
            producer.publishTopic(exchange, "white", "Hi".getBytes());
            producer.publishTopic(exchange, "color.", "How".getBytes());
            assert true;
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            assert false;
        }
    }
}