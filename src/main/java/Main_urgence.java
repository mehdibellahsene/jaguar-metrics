import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Main_urgence {
    private static final String BROKER_HOST = System.getenv("broker_host");
    private static final String EXCHANGE_NAME = "logs";


public static void main(String[] argv) throws Exception {
        // Connexion au courtier RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(BROKER_HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Déclaration de la file d'attente
        channel.queueDeclare(EXCHANGE_NAME, false, false, false, null);
        System.out.println(" [*] En attente de messages. Pour sortir, appuyez sur CTRL+C");

        // Callback pour traiter les messages reçus
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Reçu '" + message + "'");
            
            Cons_BDD_insert bdd = new Cons_BDD_insert();
            // Insérer le message dans la base de données
            bdd.insertIntoDatabase(message);
        };

        // Consommation des messages de la file d'attente
        channel.basicConsume(EXCHANGE_NAME, true, deliverCallback, consumerTag -> { });
    }
}