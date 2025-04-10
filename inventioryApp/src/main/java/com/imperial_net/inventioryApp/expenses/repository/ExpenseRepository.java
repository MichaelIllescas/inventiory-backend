package com.imperial_net.inventioryApp.expenses.repository;

import com.imperial_net.inventioryApp.expenses.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para acceder a datos relacionados con los gastos.
 * Proporciona métodos personalizados para filtrar por fecha y usuario.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Obtiene todos los gastos registrados por un usuario específico.
     *
     * @param userId ID del usuario.
     * @return lista de gastos del usuario.
     */
    List<Expense> findAllByCreatedBy_Id(Long userId);

    /**
     * Busca los gastos que se encuentran entre dos fechas (sin filtrar por usuario).
     *
     * @param startDate fecha de inicio.
     * @param endDate   fecha de fin.
     * @return lista de gastos en el rango de fechas.
     */
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Busca los gastos realizados en una fecha exacta.
     *
     * @param date fecha exacta.
     * @return lista de gastos realizados ese día.
     */
    List<Expense> findByDate(LocalDate date);

    /**
     * Busca los gastos realizados por un usuario entre dos fechas.
     *
     * @param startDate fecha de inicio.
     * @param endDate   fecha de fin.
     * @param userId    ID del usuario.
     * @return lista de gastos del usuario en ese rango de fechas.
     */
    @Query("SELECT e FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate AND e.createdBy.id = :userId")
    List<Expense> findExpensesBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId
    );

    /**
     * Cuenta la cantidad total de gastos registrados por un usuario.
     *
     * @param userId ID del usuario.
     * @return número total de gastos registrados por el usuario.
     */
    Long countByCreatedBy_Id(Long userId);
}
