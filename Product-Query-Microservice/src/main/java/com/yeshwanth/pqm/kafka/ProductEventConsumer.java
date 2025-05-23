package com.yeshwanth.pqm.kafka;

import com.yeshwanth.pqm.dto.ProductEvent;
import com.yeshwanth.pqm.model.Product;
import com.yeshwanth.pqm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "${spring.kafka.topics.product-events}")
    public void consumeProductEvent(ProductEvent event) {
        log.info("Received product event: {}", event);
        
        switch (event.getEventType()) {
            case "CREATED":
                handleProductCreated(event.getProduct());
                break;
            case "UPDATED":
                handleProductUpdated(event.getProduct());
                break;
            case "DELETED":
                handleProductDeleted(event.getProduct());
                break;
            default:
                log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handleProductCreated(Product product) {
        log.info("Handling product created event for product: {}", product);
        // Ensure created time is set
        if (product.getCreatedTime() == null) {
            product.setCreatedTime(LocalDateTime.now());
        }
        if (product.getUpdatedTime() == null) {
            product.setUpdatedTime(product.getCreatedTime());
        }
        productRepository.save(product);
        log.info("Product saved successfully: {}", product);
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
                    if (product.getUpdatedTime() != null) {
                        existingProduct.setUpdatedTime(product.getUpdatedTime());
                    } else {
                        existingProduct.setUpdatedTime(LocalDateTime.now());
                    }
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