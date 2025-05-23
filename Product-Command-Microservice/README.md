# Product Command Microservice

This microservice is part of a CQRS architecture and handles commands for product management.

## Features

- Create, update, and delete products
- Publish events to Kafka for synchronization with query services
- OpenAPI documentation with Swagger UI

## Technologies

- Spring Boot 3.5.0
- Spring Data JPA
- MySQL
- Apache Kafka
- Swagger OpenAPI 3.0

## Getting Started

### Prerequisites

- Java 17
- Maven
- Docker and Docker Compose (for containerized deployment)

### Local Development

1. Start MySQL and Kafka locally or use the provided Docker Compose file:
   ```
   docker-compose up -d
   ```

2. Run the application:
   ```
   mvn spring-boot:run
   ```

3. Access the API documentation:
   ```
   http://localhost:8081/swagger-ui.html
   ```

### Docker Deployment

#### Using Docker Compose (for all services)

1. Modify the `application.yml` file:
   - Comment out the local development configuration sections
   - Uncomment the Docker deployment configuration sections

2. Build the application:
   ```
   mvn clean package
   ```

3. Start all services using Docker Compose:
   ```
   docker-compose up -d
   ```

#### Building and Running the Application Container Only

1. Build the application:
   ```
   mvn clean package
   ```

2. Build the Docker image:
   ```
   docker build -t product-command-service .
   ```

3. Run the Docker container:
   ```
   docker run -p 8081:8081 --name product-command-container product-command-service
   ```

   Note: When running the container alone, make sure MySQL and Kafka are accessible to the container. You may need to use network flags or environment variables to configure the connection.

## API Documentation

The API documentation is available at:
- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/api-docs

## Docker Services

The Docker Compose file includes the following services:

- **MySQL**: Database for storing product information
  - Port: 3306
  - Credentials: root/java
  - Database: product_command_db

- **Zookeeper**: Required for Kafka
  - Port: 2181

- **Kafka**: Message broker for event publishing
  - Ports: 9092 (internal), 29092 (external)

- **Kafka UI**: Web interface for monitoring Kafka
  - Port: 8080
  - URL: http://localhost:8080

## Configuration

The application can be configured using the `application.yml` file. It includes:

- Database connection settings
- Kafka producer configuration
- OpenAPI/Swagger settings

The file contains commented sections for both local development and Docker deployment.
