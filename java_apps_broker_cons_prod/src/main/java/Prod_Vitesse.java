import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Prod_Vitesse {

    private static final String EXCHANGE_NAME = "logs";
    private static final String ROUTING_KEY = "logs.vitesse";
    private static final Random random = new Random();
    private static final String BROKER_HOST = System.getenv("broker_host");

    public static double generateSpeed() {
        // 80% chance to generate a normal training speed
        if (random.nextDouble() < 0.7) {
            // Generate a speed in the normal training range (8 to 12 km/h)
            return 8 + (12 - 8) * random.nextDouble();
        } else {
            // 20% chance to generate a speed outside the normal training range
            // Decide randomly whether the speed will be for a sprint interval or a recovery run
            if (random.nextBoolean()) {
                // Generate a sprint interval speed, e.g., 15 to 20 km/h
                return 15 + (20 - 15) * random.nextDouble();
            } else {
                // Generate a recovery run speed, e.g., 5 to 8 km/h
                return 5 + (8 - 5) * random.nextDouble();
            }
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
					double vitesse = generateTemperature();
					String messageType = "VITESSE";
										String message = String.format("{\"data\": %.2f, \"type\": \"%s\"}", temperature, messageType);
                    System.out.println("Routing key : " + ROUTING_KEY + " ; message : " + message);

                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
                    System.out.println(" [x] Sent '" + message + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            scheduler.scheduleAtFixedRate(produceMessageTask, 0, 2, TimeUnit.SECONDS);

            // Run the scheduler for 1 hour (for example)
            scheduler.awaitTermination(1, TimeUnit.HOURS);
            scheduler.shutdown();
        }
    }
}
