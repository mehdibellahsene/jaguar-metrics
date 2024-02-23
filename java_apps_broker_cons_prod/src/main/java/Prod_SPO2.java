import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Prod_SPO2 {

    private static final String EXCHANGE_NAME = "logs";
    private static final String ROUTING_KEY = "logs.vital.spO2";

    private static final String BROKER_HOST = System.getenv("broker_host");
    private static final Random random = new Random();

    public static int generateSpO2() {
        // 90% chance to generate a normal value
        if (random.nextDouble() < 0.7) {
            // Generate a value in the normal range (94 to 100 inclusive)
            return 94 + random.nextInt(7); // 7 because nextInt is exclusive on the upper bound
        } else {
            return 70 + random.nextInt(14); // Adjust this range as needed
        }
    }

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(BROKER_HOST);

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            Runnable produceMessageTask = () -> {
                try {
                    int spo2Value = generateSpO2();
                    String messageType = "SPO2";

                    // Create a JSON object
                    String message = String.format("{\"data\": %d, \"type\": \"%s\"}", spo2Value, messageType);

                    System.out.println("Routing key : " + ROUTING_KEY + " ; message : " + message);

                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            scheduler.scheduleAtFixedRate(produceMessageTask, 0, 2, TimeUnit.SECONDS);

            scheduler.awaitTermination(1, TimeUnit.HOURS);
            scheduler.shutdown();
        }
    }
}
