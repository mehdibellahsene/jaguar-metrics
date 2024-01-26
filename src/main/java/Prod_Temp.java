import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Random;

public class Prod_Temp {

	private static final String EXCHANGE_NAME = "infos";
	private static final String ROUTING_KEY = "infos.temp";
    private static final Random random = new Random();
	private static final String BROKER_HOST = System.getenv("broker_host");
	public static double generateTemperature() {
        // 90% chance to generate a normal temperature
        if (random.nextDouble() < 0.9) {
            // Generate a temperature in the normal range (36.5 to 37.5 inclusive)
            // Random.nextDouble() generates a value between 0.0 and 1.0
            // Scale and shift this to the range of normal temperatures
            return 36.5 + (37.5 - 36.5) * random.nextDouble();
        } else {
            // 10% chance to generate an abnormal temperature
            // For simplicity, let's consider abnormal values to be below 36.0 or above 38.0
            // Decide randomly whether the abnormal temperature will be high or low
            if (random.nextBoolean()) {
                // Generate a high abnormal temperature, e.g., 38.1 to 39.0
                return 38.1 + (39.0 - 38.1) * random.nextDouble();
            } else {
                // Generate a low abnormal temperature, e.g., 35.0 to 36.0
                return 35.0 + (36.0 - 35.0) * random.nextDouble();
            }
        }
    }

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(BROKER_HOST);
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			String message = argv.length < 1 ? "Prod_Temp:"+ generateTemperature()  +  "°!" : String.join(" ", argv);
			System.out.println("Routing key : " + ROUTING_KEY + " ; message : " + message);

			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		}
	}

}
