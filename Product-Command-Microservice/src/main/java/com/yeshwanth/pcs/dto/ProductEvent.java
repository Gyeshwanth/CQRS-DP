package com.yeshwanth.pcs.dto;

import com.yeshwanth.pcs.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    private String eventType;
    private Product product;
}
