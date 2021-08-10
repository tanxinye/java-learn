package txy.learn.connection.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SimpleConsumer {

    private final ConnectionFactory factory;

    public SimpleConsumer(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public SimpleConsumer(String host, Integer port, String username, String password, String virtualHost) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
    }

    public Connection newConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    public void listen(String queue) {
        try {
            Connection conn = newConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, (consumerTag, message) -> {
                String msg = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.println(msg);
            }, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SimpleConsumer simpleConsumer = new SimpleConsumer("127.0.0.1", 5672, "xinye", "xinye", "/");
        simpleConsumer.listen("txy/learn/connection");
    }
}
