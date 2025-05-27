package com.yeshwanth.pcs.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    // Kafka topic name
    public static final String PRODUCT_EVENT_TOPIC = "product-event-topic";

    // Event types
    public static final String EVENT_CREATE_PRODUCT = "CREATE_PRODUCT";
    public static final String EVENT_UPDATE_PRODUCT = "UPDATE_PRODUCT";
    public static final String EVENT_DELETE_PRODUCT = "DELETE_PRODUCT";


}


