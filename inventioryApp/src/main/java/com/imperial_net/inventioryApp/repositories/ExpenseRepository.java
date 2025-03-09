package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByCreatedBy_Id(Long userId);
    void deleteById(Long id);
}
