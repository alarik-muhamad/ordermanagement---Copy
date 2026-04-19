package com.example.ordermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ordermanagement.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
