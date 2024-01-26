import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Random;

public class Prod_SPO2 {

	private static final String EXCHANGE_NAME = "logs";
	private static final String ROUTING_KEY = "#my_route";

	private static final String BROKER_HOST = System.getenv("broker_host");
    private static final Random random = new Random();
	public static int generateSpO2() {
        // 90% chance to generate a normal value
        if (random.nextDouble() < 0.8) {
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
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

			String message = argv.length < 1 ? "Prod_SPO2: " + generateSpO2() + " % " : String.join(" ", argv);

			System.out.println("Routing key : " + ROUTING_KEY + " ; message : " + message);

			channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		}
	}

}
