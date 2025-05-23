package com.yeshwanth.pqm.dto;

import com.yeshwanth.pqm.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {

    private String eventType;
    private Product product;
}
