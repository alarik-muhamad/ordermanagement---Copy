package com.example.ordermanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private Integer quantityOnHand;

    @Column(nullable = false)
    private Integer quantityReserved;

    @Column(nullable = false)
    private Integer reorderLevel;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    public Inventory() {}

    public Inventory(Product product, Integer quantityOnHand, Integer reorderLevel) {
        this.product = product;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = 0;
        this.reorderLevel = reorderLevel;
        this.lastUpdated = LocalDateTime.now();
    }

    // Derived: available = onHand - reserved
    public Integer getQuantityAvailable() {
        return quantityOnHand - quantityReserved;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Integer quantityOnHand) { this.quantityOnHand = quantityOnHand; }

    public Integer getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(Integer quantityReserved) { this.quantityReserved = quantityReserved; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
