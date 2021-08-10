package txy.learn.connection.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    private final ConnectionFactory factory;

    public Producer(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public Producer(String host, Integer port, String username, String password, String virtualHost) {
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

    public void publish(String queue, byte[] body) throws IOException, TimeoutException {
        try (Connection conn = newConnection(); Channel channel = conn.createChannel()) {
            channel.queueDeclare(queue, true, false, false, null);
            channel.basicPublish("", queue, null, body);
        }
    }

    public void publishFanOut(String exchange, byte[] body) throws IOException, TimeoutException {
        try (Connection conn = newConnection(); Channel channel = conn.createChannel()) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);
            channel.basicPublish(exchange, "", null, body);
        }
    }

    public void publishDirect(String exchange, String routingKey, byte[] body) throws IOException, TimeoutException {
        try (Connection conn = newConnection(); Channel channel = conn.createChannel()) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
            channel.basicPublish(exchange, routingKey, null, body);
        }
    }

    public void publishTopic(String exchange, String routingKey, byte[] body) throws IOException, TimeoutException {
        try (Connection conn = newConnection(); Channel channel = conn.createChannel()) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
            channel.basicPublish(exchange, routingKey, null, body);
        }
    }

}
