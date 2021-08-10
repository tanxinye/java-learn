package txy.learn.connection.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class WorkerConsumer {

    private final ConnectionFactory factory;

    public WorkerConsumer(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
    }

    public WorkerConsumer(String host, Integer port, String username, String password, String virtualHost) {
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
            channel.queueDeclare(queue, true, false, false, null);
            channel.basicConsume(queue, false, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    for (char ch : message.toCharArray()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
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
        WorkerConsumer workerConsumer = new WorkerConsumer("127.0.0.1", 5672, "xinye", "xinye", "/");
        workerConsumer.listen("txy/learn/connection");
    }
}
