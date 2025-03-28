package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByCreatedBy_Id(Long userId);
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List <Expense> findByDate (LocalDate date);
    @Query("SELECT e FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate AND e.createdBy.id = :userId ")
    List<Expense> findExpensesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                           @Param("userId") Long userId);
}
