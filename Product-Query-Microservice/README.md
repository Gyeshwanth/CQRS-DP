# Product Query Microservice

This is a Spring Boot microservice for querying product data as part of a CQRS architecture.

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

### Testing Kafka Integration

A test class `ProductEventConsumerTest` is provided to test the Kafka integration. It includes tests for all event types and edge cases.

## Testing with JUnit

In addition to the HTTP Client, you can also test the endpoints programmatically using JUnit tests. A sample test class is provided at `src/test/java/com/yeshwanth/productquerymicroservice/controller/ProductControllerTest.java`.

### Running the JUnit Tests

1. Open the test class in IntelliJ IDEA
2. Click the green "Run" button (▶️) next to the class name to run all tests
3. Alternatively, click the green "Run" button next to individual test methods to run specific tests
4. The test results will appear in the "Run" panel at the bottom of the IDE

### Test Methods

The test class includes the following test methods:

1. **testGetAllProducts**
   - Tests the endpoint to get all products
   - Verifies that the response status is 200 OK and the response body is not null

2. **testGetProductById**
   - Tests the endpoint to get a product by ID
   - Assumes there's a product with ID 1 in the database
   - If the product exists, verifies that the response body is not null

3. **testGetProductByIdNotFound**
   - Tests the endpoint with a non-existent ID (999)
   - Verifies that the response status is 404 Not Found

### Debug Logging

The test class includes debug logging to help you understand what's happening during the tests. Look for lines starting with `[DEBUG_LOG]` in the console output.
