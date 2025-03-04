package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findAllByCreatedBy_Id(Long userId);
    Optional<Provider>findByName(String name);
    Optional<Provider>findByTaxId(String taxId);
}
