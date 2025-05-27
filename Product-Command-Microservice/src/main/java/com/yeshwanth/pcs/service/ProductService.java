package com.yeshwanth.pcs.service;

import com.yeshwanth.pcs.config.KafkaConfig;
import com.yeshwanth.pcs.dto.ProductEvent;
import com.yeshwanth.pcs.dto.ProductRequest;
import com.yeshwanth.pcs.dto.ProductResponse;
import com.yeshwanth.pcs.model.Product;
import com.yeshwanth.pcs.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String,ProductEvent> kafkaTemplate;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        // Check if product with same name already exists
        if (productRepository.existsByName(productRequest.getName())) {
            throw new IllegalArgumentException("Product with name " + productRequest.getName() + " already exists");
        }

        // Create new product
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());

        // Save product
        Product savedProduct = productRepository.save(product);

        // Create event
        ProductEvent productEvent = ProductEvent.builder()
                .eventType(KafkaConfig.EVENT_CREATE_PRODUCT)
                .product(savedProduct)
                .build();

        // Send to Kafka with error handling
        try {
            CompletableFuture<SendResult<String, ProductEvent>> future = 
                    kafkaTemplate.send(KafkaConfig.PRODUCT_EVENT_TOPIC, productEvent);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Unable to send product created event to Kafka: {}", ex.getMessage());
                } else {
                    log.info("Product created event sent to Kafka: {}", result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("Exception occurred while sending product created event to Kafka: {}", e.getMessage());
        }

        // Return response
        return mapToProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Update product fields
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());

        // Save updated product
        Product updatedProduct = productRepository.save(product);

        // Create event
        ProductEvent productEvent = ProductEvent.builder()
                .eventType(KafkaConfig.EVENT_UPDATE_PRODUCT)
                .product(updatedProduct)
                .build();

        // Send to Kafka with error handling
        try {
            CompletableFuture<SendResult<String, ProductEvent>> future = 
                    kafkaTemplate.send(KafkaConfig.PRODUCT_EVENT_TOPIC, productEvent);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Unable to send product updated event to Kafka: {}", ex.getMessage());
                } else {
                    log.info("Product updated event sent to Kafka: {}", result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("Exception occurred while sending product updated event to Kafka: {}", e.getMessage());
        }

        // Return response
        return mapToProductResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        // Get product before deletion
        Product product = productRepository.findById(id).get();

        // Delete product
        productRepository.deleteById(id);

        // Create event
        ProductEvent productEvent = ProductEvent.builder()
                .eventType(KafkaConfig.EVENT_DELETE_PRODUCT)
                .product(product)
                .build();

        // Send to Kafka with error handling
        try {
            CompletableFuture<SendResult<String, ProductEvent>> future = 
                    kafkaTemplate.send(KafkaConfig.PRODUCT_EVENT_TOPIC, productEvent);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Unable to send product deleted event to Kafka: {}", ex.getMessage());
                } else {
                    log.info("Product deleted event sent to Kafka: {}", result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            log.error("Exception occurred while sending product deleted event to Kafka: {}", e.getMessage());
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}
