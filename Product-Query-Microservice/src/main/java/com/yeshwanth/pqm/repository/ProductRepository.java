package com.yeshwanth.pqm.repository;

import com.yeshwanth.pqm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Additional query methods can be added here if needed
}
