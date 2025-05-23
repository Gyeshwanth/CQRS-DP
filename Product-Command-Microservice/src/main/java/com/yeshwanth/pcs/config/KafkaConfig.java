package com.yeshwanth.pcs.config;

import com.yeshwanth.pcs.dto.ProductEventDTO;
import com.yeshwanth.pcs.model.Product;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    // Kafka topic name
    public static final String PRODUCT_EVENT_TOPIC = " ";
    
    // Event types
    public static final String EVENT_CREATE_PRODUCT = "CREATE_PRODUCT";
    public static final String EVENT_UPDATE_PRODUCT = "UPDATE_PRODUCT";
    public static final String EVENT_DELETE_PRODUCT = "DELETE_PRODUCT";
    
    // Mapper method to convert Product entity to ProductEventDTO
    public static ProductEventDTO mapToProductEventDTO(Product product) {
        return ProductEventDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}