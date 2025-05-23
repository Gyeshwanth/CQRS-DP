package com.yeshwanth.pqm.kafka;

import com.yeshwanth.pqm.dto.ProductEvent;
import com.yeshwanth.pqm.model.Product;
import com.yeshwanth.pqm.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductEventConsumerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductEventConsumer productEventConsumer;

    private Product testProduct;
    private ProductEvent testEvent;

    @BeforeEach
    void setUp() {
        // Create a test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setQuantity(10);
        testProduct.setCreatedTime(LocalDateTime.now());
        testProduct.setUpdatedTime(LocalDateTime.now());
    }

    @Test
    void testHandleProductCreatedEvent() {
        // Create a CREATED event
        testEvent = new ProductEvent("CREATED", testProduct);
        
        // Configure mock
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Call the method
        productEventConsumer.consumeProductEvent(testEvent);
        
        // Verify that save was called
        verify(productRepository, times(1)).save(any(Product.class));
        System.out.println("[DEBUG_LOG] Product created event handled successfully");
    }

    @Test
    void testHandleProductUpdatedEvent() {
        // Create an UPDATED event
        testEvent = new ProductEvent("UPDATED", testProduct);
        
        // Configure mock
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Call the method
        productEventConsumer.consumeProductEvent(testEvent);
        
        // Verify that findById and save were called
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        System.out.println("[DEBUG_LOG] Product updated event handled successfully");
    }

    @Test
    void testHandleProductUpdatedEventProductNotFound() {
        // Create an UPDATED event
        testEvent = new ProductEvent("UPDATED", testProduct);
        
        // Configure mock to return empty
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // Call the method
        productEventConsumer.consumeProductEvent(testEvent);
        
        // Verify that findById and save were called
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        System.out.println("[DEBUG_LOG] Product updated event with non-existent product handled successfully");
    }

    @Test
    void testHandleProductDeletedEvent() {
        // Create a DELETED event
        testEvent = new ProductEvent("DELETED", testProduct);
        
        // Configure mock
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(any(Product.class));
        
        // Call the method
        productEventConsumer.consumeProductEvent(testEvent);
        
        // Verify that findById and delete were called
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(any(Product.class));
        System.out.println("[DEBUG_LOG] Product deleted event handled successfully");
    }

    @Test
    void testHandleProductDeletedEventProductNotFound() {
        // Create a DELETED event
        testEvent = new ProductEvent("DELETED", testProduct);
        
        // Configure mock to return empty
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Call the method
        productEventConsumer.consumeProductEvent(testEvent);
        
        // Verify that findById was called but delete was not
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).delete(any(Product.class));
        System.out.println("[DEBUG_LOG] Product deleted event with non-existent product handled successfully");
    }

    @Test
    void testHandleUnknownEventType() {
        // Create an event with unknown type
        testEvent = new ProductEvent("UNKNOWN", testProduct);
        
        // Call the method
        productEventConsumer.consumeProductEvent(testEvent);
        
        // Verify that no repository methods were called
        verifyNoInteractions(productRepository);
        System.out.println("[DEBUG_LOG] Unknown event type handled successfully");
    }
}