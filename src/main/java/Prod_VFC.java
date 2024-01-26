import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Prod_VFC {

	private static final String EXCHANGE_NAME = "logs";
	private static final String ROUTING_KEY = "logs.vital.vfc";
    private static final Random random = new Random();
	private static final String BROKER_HOST = System.getenv("broker_host");

	public static int generateHeartRate() {
        // 90% chance to generate a normal heart rate
        if (random.nextDouble() < 0.7) {
            // Generate a heart rate in the normal range (60 to 100 inclusive)
            return 60 + random.nextInt(41); // 41 because nextInt is exclusive on the upper bound
        } else {
            // 10% chance to generate an abnormal heart rate
            // Decide randomly whether the abnormal rate will be low or high
            if (random.nextBoolean()) {
                // Generate a low abnormal heart rate, e.g., 45 to 59
                return 45 + random.nextInt(15); // Adjust this range as needed
            } else {
                // Generate a high abnormal heart rate, e.g., 101 to 120
                return 101 + random.nextInt(20); // Adjust this range as needed
            }
        }
	}
	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(BROKER_HOST);
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			String message = argv.length < 1 ? "Prod_VFC: "+  generateHeartRate() + " bpm!" : String.join(" ", argv);
			System.out.println("Routing key : " + ROUTING_KEY + " ; message : " + message);

			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		}
	}

}
