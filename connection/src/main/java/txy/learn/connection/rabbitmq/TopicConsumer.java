package txy.learn.connection.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class TopicConsumer {

    private final ConnectionFactory factory;

    public TopicConsumer(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public TopicConsumer(String host, Integer port, String username, String password, String virtualHost) {
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

    public void listen(String exchange, String[] routingKeys) {
        try {
            Connection conn = newConnection();
            Channel channel = conn.createChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);

            String queueName = channel.queueDeclare().getQueue();

            for (String key : routingKeys) {
                channel.queueBind(queueName, exchange, key);
            }

            channel.basicConsume(queueName, false, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    String routingKey = delivery.getEnvelope().getRoutingKey();
                    System.out.println("finish routingKey：" + routingKey + " " + message);
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
        TopicConsumer topicConsumer = new TopicConsumer("127.0.0.1", 5672, "xinye", "xinye", "/");
        topicConsumer.listen("test-topic", new String[]{"color.*"});
    }
}
