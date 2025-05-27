# Product Command Microservice

This microservice is part of a CQRS (Command Query Responsibility Segregation) architecture and handles commands for product management.

## Architecture Overview

This application follows the CQRS architectural pattern, which separates the command (write) operations from the query (read) operations. This separation allows for:

- **Optimized Data Models**: Each side can use a data model optimized for its specific needs
- **Scalability**: Command and query services can be scaled independently
- **Performance**: Query-side can be optimized for read operations without affecting write operations

In this architecture:
- **Command Side** (this microservice): Handles create, update, and delete operations
- **Query Side** (separate microservice): Handles read operations and queries

The two sides are kept in sync through event publishing using Apache Kafka. When a command operation (create, update, delete) is performed, this service:
1. Executes the operation on its database
2. Creates an event with the operation details
3. Publishes the event to Kafka
4. The query-side microservice consumes these events and updates its read model accordingly

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

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/products | Create a new product |
| PUT | /api/products/{id} | Update an existing product |
| DELETE | /api/products/{id} | Delete a product |

### Request/Response Examples

#### Create Product
**Request:**
```http
POST /api/products
Content-Type: application/json

{
  "name": "Test Product",
  "description": "This is a test product",
  "price": 99.99,
  "quantity": 10
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Test Product",
  "description": "This is a test product",
  "price": 99.99,
  "quantity": 10
}
```

#### Update Product
**Request:**
```http
PUT /api/products/1
Content-Type: application/json

{
  "name": "Updated Test Product",
  "description": "This is an updated test product",
  "price": 149.99,
  "quantity": 5
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Updated Test Product",
  "description": "This is an updated test product",
  "price": 149.99,
  "quantity": 5
}
```

#### Delete Product
**Request:**
```http
DELETE /api/products/1
```

**Response:**
```
204 No Content
```

### Error Handling

The API returns appropriate HTTP status codes and error messages:

| Status Code | Description |
|-------------|-------------|
| 200 | OK - The request was successful |
| 201 | Created - The resource was successfully created |
| 204 | No Content - The request was successful (for DELETE operations) |
| 400 | Bad Request - The request was invalid (e.g., product with same name already exists) |
| 500 | Internal Server Error - An unexpected error occurred |

**Error Response Example:**
```json
{
  "timestamp": "2023-06-15T10:30:45.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Product with name Test Product already exists",
  "path": "/api/products"
}
```

## Testing the Application

### HTTP Client Tests

The project includes HTTP client test files in the `src/test/http` directory that can be used to test the API endpoints. These files can be executed using tools like IntelliJ IDEA's HTTP Client, VS Code's REST Client, or Postman.

To run the tests:

1. Start the application locally
2. Open the `product-api-tests.http` file in your IDE
3. Execute the requests individually or as a collection

The test file includes requests for:
- Creating a new product
- Updating an existing product
- Deleting a product

### Environment Configuration

The HTTP tests use environment variables defined in `http-client.env.json`. You can modify this file to match your environment:

```json
{
  "local": {
    "host": "localhost:8081",
    "productId": "1"
  }
}
```

### Manual Testing

You can also test the API manually using tools like cURL, Postman, or any HTTP client:

#### Create a Product
```bash
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "This is a test product",
    "price": 99.99,
    "quantity": 10
  }'
```

#### Update a Product
```bash
curl -X PUT http://localhost:8081/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test Product",
    "description": "This is an updated test product",
    "price": 149.99,
    "quantity": 5
  }'
```

#### Delete a Product
```bash
curl -X DELETE http://localhost:8081/api/products/1
```

### Verifying Kafka Events

To verify that events are being published to Kafka:

1. Start the Kafka environment (using Docker Compose)
2. Use the Kafka UI at http://localhost:8080 to monitor topics and messages
3. Alternatively, use the Kafka console consumer to view messages:
   ```bash
   docker exec -it kafka kafka-console-consumer.sh --topic product-events --from-beginning --bootstrap-server localhost:9092
   ```
4. Perform operations (create, update, delete) through the API
5. Verify that corresponding events appear in the Kafka topic

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

## Kafka Configuration and Commands

### Kafka Configuration

This microservice uses Kafka to publish product events when products are created, updated, or deleted. The Kafka configuration is defined in:

1. **application.yml**:
   ```yaml
   spring:
     kafka:
       producer:
         bootstrap-servers: localhost:9092
         key-serializer: org.apache.kafka.common.serialization.StringSerializer
         value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
   ```

2. **KafkaConfig.java**:
   - Defines event types: CREATE_PRODUCT, UPDATE_PRODUCT, DELETE_PRODUCT
   - Defines the topic name for product events
   - Provides a mapper method to convert Product entities to ProductEventDTO objects

### Kafka Event Structure

Events published to Kafka have the following structure:
```json
{
  "eventType": "CREATE_PRODUCT|UPDATE_PRODUCT|DELETE_PRODUCT",
  "product": {
    "id": 1,
    "name": "Product Name",
    "description": "Product Description",
    "price": 99.99,
    "quantity": 10
  }
}
```

### Useful Kafka Commands

#### List Topics
```bash
docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
```

#### Create a Topic
```bash
docker exec -it kafka kafka-topics.sh --create --topic product-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Describe a Topic
```bash
docker exec -it kafka kafka-topics.sh --describe --topic product-events --bootstrap-server localhost:9092
```

#### Consume Messages from a Topic
```bash
docker exec -it kafka kafka-console-consumer.sh --topic product-events --from-beginning --bootstrap-server localhost:9092
```

#### Produce Messages to a Topic
```bash
docker exec -it kafka kafka-console-producer.sh --topic product-events --bootstrap-server localhost:9092
```

#### Delete a Topic
```bash
docker exec -it kafka kafka-topics.sh --delete --topic product-events --bootstrap-server localhost:9092
```
