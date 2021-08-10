package txy.learn.connection.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class PubSubConsumer {

    private final ConnectionFactory factory;

    public PubSubConsumer(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public PubSubConsumer(String host, Integer port, String username, String password, String virtualHost) {
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

    public void listen(String exchange) {
        try {
            Connection conn = newConnection();
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);

            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, "");
            channel.basicConsume(queueName, false, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    System.out.println("finish " + message);
                } finally {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }

            }, consumerTag -> {
            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PubSubConsumer pubSubConsumer = new PubSubConsumer("127.0.0.1", 5672, "xinye", "xinye", "/");
        pubSubConsumer.listen("test-exchange");
    }
}
