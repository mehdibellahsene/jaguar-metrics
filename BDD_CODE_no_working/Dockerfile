# Utilisation de l'image OpenJDK 17 officielle
FROM openjdk:17-alpine

# Définition du répertoire de travail dans le conteneur
WORKDIR /app

# Copie du fichier JAR de l'application dans le conteneur
COPY ./app/app-jar-with-dependencies.jar /app/app-jar-with-dependencies.jar

# Commande pour exécuter l'application lorsque le conteneur démarre
CMD ["java", "-jar", "/app/app-jar-with-dependencies.jar"]
