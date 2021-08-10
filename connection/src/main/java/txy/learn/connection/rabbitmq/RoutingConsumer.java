package txy.learn.connection.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RoutingConsumer {

    private final ConnectionFactory factory;

    public RoutingConsumer(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public RoutingConsumer(String host, Integer port, String username, String password, String virtualHost) {
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
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);

            String queueName = channel.queueDeclare().getQueue();

            for (String key : routingKeys) {
                channel.queueBind(queueName, exchange, key);
            }

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
        RoutingConsumer routingConsumer = new RoutingConsumer("127.0.0.1", 5672, "xinye", "xinye", "/");
        routingConsumer.listen("test-routing", new String[]{"blue", "black", "white"});
    }
}
