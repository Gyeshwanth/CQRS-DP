# Product Query Microservice

This is a Spring Boot microservice for querying product data as part of a CQRS (Command Query Responsibility Segregation) architecture.

## Overview

This microservice is part of a CQRS architecture that separates read and write operations for better scalability and performance. The Product Query Microservice is responsible for handling read operations (queries) for product data, while a separate Product Command Microservice handles write operations (commands).

### CQRS Architecture

In this implementation:
- **Command Side**: Handles create, update, and delete operations (Product Command Microservice)
- **Query Side**: Handles read operations and maintains a denormalized view of the data (This microservice)
- **Event Communication**: Kafka is used as the event bus to propagate changes from the command side to the query side

### Key Features

- RESTful API for querying product data
- Kafka consumer for processing product events
- Optimistic locking for handling concurrent updates
- MySQL database for storing the query model
- Swagger/OpenAPI documentation
- Docker support for easy deployment

## Project Structure

The project follows a standard Spring Boot application structure:

```
src/main/java/com/yeshwanth/pqm/
├── controller/           # REST API controllers
├── dto/                  # Data Transfer Objects
├── kafka/                # Kafka consumer and event handling
├── model/                # Entity models
├── repository/           # Data access layer
├── service/              # Business logic
└── ProductQueryMicroserviceApplication.java  # Main application class
```

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Kafka 3.0 or higher

## Setup and Installation

### Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/product-query-microservice.git
   cd product-query-microservice
   ```

2. Configure the application:
   - Edit `src/main/resources/application.yml` to set up your database and Kafka connection
   - Uncomment the local development configuration sections

3. Build the application:
   ```bash
   ./mvnw clean package
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. The application will be available at http://localhost:8082

## Testing Endpoints with IntelliJ HTTP Client

IntelliJ IDEA provides a built-in HTTP Client that allows you to test REST endpoints directly from the IDE without needing external tools like Postman.

### How to Use the HTTP Client

1. Open the HTTP request file located at `src/test/http/product-api-tests.http`
2. You'll see predefined HTTP requests for testing the product API endpoints
3. Click the green "Run" button (▶️) next to each request to execute it
4. The response will appear in a separate panel on the right

### Available Endpoints

The following endpoints are available for testing:

1. **Get All Products**
   - URL: `GET http://localhost:8082/api/products`
   - Description: Retrieves a list of all products

2. **Get Product by ID**
   - URL: `GET http://localhost:8082/api/products/{id}`
   - Description: Retrieves a specific product by its ID
   - Example: `GET http://localhost:8082/api/products/1`

### Tips for Using the HTTP Client

- Make sure the application is running before executing the requests
- You can modify the requests as needed (e.g., change the product ID)
- The HTTP Client supports environment variables, which can be useful for testing in different environments
- You can add more requests to the file as needed

### Using Environment Variables

The HTTP requests use environment variables to make them more flexible. Here's how to use them:

1. The environment configuration is defined in `src/test/http/http-client.env.json`
2. Two environments are defined: `development` and `production`
3. To select an environment, use the environment selector dropdown in the HTTP Client toolbar
4. By default, the `development` environment is used, which points to `localhost:8082`
5. You can modify the environment configuration as needed for your setup

### Troubleshooting

- If you get a connection refused error, make sure the application is running
- If you get a 404 error, check that the endpoint URL is correct
- If you get a 500 error, check the application logs for more details

## API Documentation with Swagger/OpenAPI

The application includes Swagger/OpenAPI documentation for the REST API endpoints. You can access the documentation at:

- Swagger UI: http://localhost:8082/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/api-docs

### Features

- Interactive API documentation
- Try out API endpoints directly from the browser
- Detailed information about request/response models
- Authentication support

## Docker Compose Setup

The application includes a Docker Compose configuration for easy deployment of all required services.

### Services Included

1. **MySQL** - Database for storing product data
2. **Zookeeper** - Required for Kafka
3. **Kafka** - Message broker for event processing
4. **Kafka UI** - Web interface for managing Kafka topics and messages

### Running with Docker Compose

1. Make sure Docker and Docker Compose are installed on your system
2. Navigate to the project root directory
3. Run the following command to start all services:

```bash
docker-compose up -d
```

4. To stop all services:

```bash
docker-compose down
```

### Configuration for Docker

The application.yml file includes configuration for both local development and Docker deployment:

- For local development: Use the localhost URLs (uncomment the local development sections)
- For Docker deployment: Use the Docker service names (uncomment the Docker configuration sections)

## Docker Image

The application includes a Dockerfile for building a Docker image of the service.

### Building the Docker Image

To build the Docker image:

```bash
docker build -t product-query-service:latest .
```

### Running the Docker Image

To run the Docker image:

```bash
docker run -p 8082:8082 --name product-query-service product-query-service:latest
```

### Docker Image Details

- Base Image: Eclipse Temurin JRE 17 Alpine
- Working Directory: /app
- Exposed Port: 8082
- Entrypoint: Java application
- Multi-stage build for optimized image size

### Integration with Docker Compose

The Docker image can be integrated with the Docker Compose setup by adding it to the docker-compose.yml file:

```yaml
services:
  product-query-service:
    build: .
    container_name: product-query-service
    ports:
      - "8082:8082"
    depends_on:
      - mysql
      - kafka
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

## Database Configuration

The application is configured to use MySQL. The database configuration can be found in `src/main/resources/application.yml`.

## Kafka Integration

This microservice uses Kafka to receive product events from the command service as part of the CQRS architecture. The Kafka configuration can be found in `src/main/resources/application.yml`.

### Kafka Configuration

The following Kafka configuration is used:

#### Local Development
- Bootstrap servers: localhost:9092
- Consumer group ID: product-query-group
- Auto offset reset: earliest
- Topic: product-event-topic

#### Docker Deployment
- Bootstrap servers: kafka:29092
- Consumer group ID: product-query-group
- Auto offset reset: earliest
- Topic: product-event-topic

### Event Types

The service handles the following event types:

1. **CREATED**: When a new product is created in the command service
2. **UPDATED**: When a product is updated in the command service
3. **DELETED**: When a product is deleted from the command service

### Event Processing

Events are processed by the `ProductEventConsumer` class, which:

1. Listens to the product-events topic
2. Deserializes the incoming JSON messages into `ProductEvent` objects
3. Processes the events based on their type
4. Updates the query database accordingly

### Kafka Commands

Here are some useful Kafka commands for working with this microservice:

#### List Topics
```bash
# Local development
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# Docker environment
docker exec -it kafka kafka-topics.sh --list --bootstrap-server kafka:29092
```

#### Create Topic
```bash
# Create the product-event-topic with 1 partition and replication factor of 1
docker exec -it kafka kafka-topics.sh --create --topic product-event-topic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092
```

#### Describe Topic
```bash
# Get details about the product-event-topic
docker exec -it kafka kafka-topics.sh --describe --topic product-event-topic --bootstrap-server localhost:9092
```

#### Produce Test Messages
```bash
# Produce a test message to the product-event-topic
docker exec -it kafka kafka-console-producer.sh --topic product-event-topic --bootstrap-server localhost:9092
```

Then enter a JSON message in the following format:
```json
{"eventType":"CREATED","product":{"id":1,"name":"Test Product","description":"A test product","price":19.99,"quantity":10}}
```

#### Consume Messages
```bash
# Consume messages from the product-event-topic
docker exec -it kafka kafka-console-consumer.sh --topic product-event-topic --from-beginning --bootstrap-server localhost:9092
```

#### List Consumer Groups
```bash
# List all consumer groups
docker exec -it kafka kafka-consumer-groups.sh --list --bootstrap-server localhost:9092
```

#### Describe Consumer Group
```bash
# Get details about the product-query-group consumer group
docker exec -it kafka kafka-consumer-groups.sh --describe --group product-query-group --bootstrap-server localhost:9092
```

#### Reset Consumer Group Offsets
```bash
# Reset the offsets for the product-query-group to the beginning of the topic
docker exec -it kafka kafka-consumer-groups.sh --reset-offsets --to-earliest --execute --group product-query-group --topic product-event-topic --bootstrap-server localhost:9092
```

## Optimistic Locking Implementation

This microservice implements optimistic locking to handle concurrent updates to product data. Optimistic locking is a strategy where the application assumes that multiple transactions can complete without affecting each other, and only checks for conflicts at commit time.

### How It Works

1. The `Product` entity includes a `version` field annotated with `@Version`:
   ```
   @Entity
   public class Product {
       // Other fields...

       @Version
       private Long version;
   }
   ```

2. When a product event is received from Kafka, the `ProductEventConsumer` processes it and updates the database.

3. If two consumers try to update the same product simultaneously, the JPA provider (Hibernate) will check the version number and throw an `OptimisticLockingFailureException` if the version has changed.

4. The `ProductEventConsumer` catches this exception and logs it without rethrowing, allowing Kafka to continue processing other messages:

```
try {
    // Process event...
} catch (OptimisticLockingFailureException e) {
    log.warn("Optimistic locking exception occurred while processing event: {}", event, e);
    log.info("This is expected in a concurrent environment and the event will be processed by another consumer");
}
```

### Benefits

- Prevents data corruption in high-concurrency scenarios
- Allows for better scalability as it doesn't require locks
- Provides a clean way to handle conflicts in an event-driven architecture

## Contributing

Contributions to the Product Query Microservice are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

### Development Guidelines

- Follow the existing code style and conventions
- Write unit tests for new features or bug fixes
- Update documentation as needed
- Make sure all tests pass before submitting a pull request

### Reporting Issues

If you find a bug or have a suggestion for improvement, please create an issue on the repository with the following information:

- A clear, descriptive title
- A detailed description of the issue or suggestion
- Steps to reproduce the issue (if applicable)
- Expected behavior and actual behavior
- Any relevant logs or screenshots

## License

This project is licensed under the MIT License - see the LICENSE file for details.
