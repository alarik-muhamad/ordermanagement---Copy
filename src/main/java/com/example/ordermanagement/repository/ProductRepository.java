package com.example.ordermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ordermanagement.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
