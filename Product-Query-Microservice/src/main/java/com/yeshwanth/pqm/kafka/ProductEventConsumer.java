package com.yeshwanth.pqm.kafka;

import com.yeshwanth.pqm.dto.ProductEvent;
import com.yeshwanth.pqm.model.Product;
import com.yeshwanth.pqm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "product-event-topic",groupId = "product-query-group")
    public void consumeProductEvent(ProductEvent event) {
        log.info("Received product event: {}", event);

        try {
            switch (event.getEventType()) {
                case "CREATE_PRODUCT":
                    handleProductCreated(event.getProduct());
                    break;
                case "UPDATE_PRODUCT":
                    handleProductUpdated(event.getProduct());
                    break;
                case "DELETE_PRODUCT":
                    handleProductDeleted(event.getProduct());
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (OptimisticLockingFailureException e) {
            // Log the optimistic locking exception but don't rethrow
            // This will prevent Kafka from retrying the message
            log.warn("Optimistic locking exception occurred while processing event: {}", event, e);
            log.info("This is expected in a concurrent environment and the event will be processed by another consumer");
        }
    }

    private void handleProductCreated(Product product) {
        log.info("Handling product created event for product: {}", product);
        Product newProduct = new Product();
        // Copy all fields except ID (which will be generated)
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setCreatedAt(product.getCreatedAt());
        newProduct.setUpdatedAt(product.getUpdatedAt());
        productRepository.save(newProduct);
        log.info("New product saved successfully: {}", newProduct);
    }


    private void handleProductUpdated(Product product) {
        log.info("Handling product updated event for product: {}", product);
        productRepository.findById(product.getId())
                .ifPresentOrElse(existingProduct -> {
                    // Update the existing product
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setQuantity(product.getQuantity());
                    // Preserve created time, update updated time
                    if (product.getUpdatedAt() != null) {
                        existingProduct.setUpdatedAt(product.getUpdatedAt());
                    } else {
                        log.info("Product from producer has null updatedAt, setting to current time");
                        existingProduct.setUpdatedAt(LocalDateTime.now());
                    }

                    // Note: We don't need to set the version field as it's managed by JPA
                    productRepository.save(existingProduct);
                    log.info("Product updated successfully: {}", existingProduct);
                }, () -> {
                    // If product doesn't exist, create it
                    log.warn("Product with ID {} not found for update, creating new", product.getId());
                    handleProductCreated(product);
                });
    }

    private void handleProductDeleted(Product product) {
        log.info("Handling product deleted event for product ID: {}", product.getId());
        productRepository.findById(product.getId())
                .ifPresentOrElse(existingProduct -> {
                    productRepository.delete(existingProduct);
                    log.info("Product deleted successfully: {}", existingProduct);
                }, () -> {
                    log.warn("Product with ID {} not found for deletion", product.getId());
                });
    }
}
