package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Brand;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    boolean existsByNameAndCodeAndBrandAndDescription(String name, String code, Brand brand, String description);
    List<Product> findAllByRegistratedBy_Id(Long userId);

    boolean existsByCode (String code);

    Optional<Product> findByCode(String code);
}
