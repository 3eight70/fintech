package ru.fintech.benchmark.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMqProducer {
    public static final String QUEUE_NAME = "test_queue";
    private final int index;
    private final Connection connection;
    private final Channel channel;

    public RabbitMqProducer(Integer index) throws IOException, TimeoutException {
        this.index = index;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("password");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    }

    public void sendMessage(String message) throws IOException {
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Сообщение отправлено в очередь: " + message);
    }

    public int getIndex() {
        return index;
    }

    public void close() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

