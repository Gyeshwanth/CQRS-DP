# Testing Product API Endpoints in IntelliJ

This directory contains HTTP request files that can be used to test the Product API endpoints directly within IntelliJ IDEA, without needing external tools like Postman.

## Prerequisites

- Make sure the application is running (either in IntelliJ or via command line)
- The application should be accessible at http://localhost:8081

## How to Use the HTTP Client in IntelliJ

1. Open the `.http` file in IntelliJ IDEA
2. You'll see a "Run" icon (green play button) next to each request
3. Click the "Run" icon to execute that specific request
4. The response will be displayed in a separate window

## Available Requests

The `product-api-tests.http` file contains the following requests:

### Create a new product
```
POST http://{{host}}/api/products
```
This request creates a new product with the provided details.

### Update an existing product
```
PUT http://{{host}}/api/products/{{productId}}
```
This request updates an existing product with the ID specified in the environment variables.

### Delete a product
```
DELETE http://{{host}}/api/products/{{productId}}
```
This request deletes the product with the ID specified in the environment variables.

## Environment Variables

The HTTP requests use environment variables to make them more flexible. These variables are defined in the `http-client.env.json` file:

```json
{
  "local": {
    "host": "localhost:8081",
    "productId": "1"
  },
  "dev": {
    "host": "dev-server:8081",
    "productId": "1"
  }
}
```

To use a specific environment:
1. Click on the environment dropdown in the top-right corner of the HTTP client window
2. Select the environment you want to use (e.g., "local" or "dev")
3. The variables will be automatically replaced in the requests

## Tips

- You can modify the request bodies to test different scenarios
- You can modify the environment variables in the `http-client.env.json` file to test with different servers or product IDs
- You can save responses for later comparison
- You can chain requests using response handling
