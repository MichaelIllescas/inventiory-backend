package com.imperial_net.inventioryApp.repositories;


import com.imperial_net.inventioryApp.models.Company;
import com.imperial_net.inventioryApp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUser(User user);

    boolean existsByUser(User user);

}