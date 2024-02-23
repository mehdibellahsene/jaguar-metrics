import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Cons_urgence {

    private static final String EXCHANGE_NAME = "logs";
    private static final String BROKER_HOST = System.getenv("broker_host");
    private static final int SEUIL_CRITIQUE = 30; // Définir le seuil critique ici

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(BROKER_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "logs.vital.*");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String routingKey = delivery.getEnvelope().getRoutingKey();

            System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");

            try {
                int numericValue = Integer.parseInt(message);

                // Check if the value is below the critical threshold
                if (numericValue < SEUIL_CRITIQUE) {
                    // Afficher un message
                    System.out.println("Seuil critique atteint! Appel des secours nécessaire.");

                    // Ajouter une logique pour appeler les secours si nécessaire
                    appelerSecours();
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid numeric value: " + message);
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    // Méthode pour simuler l'appel des secours
    private static void appelerSecours() {
        // Ajouter ici la logique pour appeler les secours (par exemple, en utilisant une API ou un service externe)
        System.out.println("Appel des secours en cours...");
    }
}
