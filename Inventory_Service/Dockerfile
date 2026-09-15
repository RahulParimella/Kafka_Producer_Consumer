FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8083
ENTRYPOINT ["java","-jar","target/Inventory_Service-0.0.1-SNAPSHOT.jar"]
