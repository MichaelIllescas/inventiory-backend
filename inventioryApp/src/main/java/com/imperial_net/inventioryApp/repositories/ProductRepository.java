package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Brand;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    boolean existsByNameAndCodeAndBrandAndDescription(String name, String code, Brand brand, String description);
    List<Product> findAllByRegistratedBy_Id(Long userId);

    List<Product> findAllByRegistratedBy_IdAndStateTrue(Long userId);

    boolean existsByCodeAndRegistratedBy_Id(String code, Long userId);


    Optional<Product> findByCode(String code);


    @Query("SELECT p FROM Product p " +
            "WHERE p.stock <= p.minStock " +
            "AND p.registratedBy.id = :userId " +
            "AND p.state = true " +
            "ORDER BY p.stock ASC")
    List<Product> findLowStockProductsByUser(@Param("userId") Long userId);
}
