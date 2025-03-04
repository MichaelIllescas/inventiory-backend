package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
