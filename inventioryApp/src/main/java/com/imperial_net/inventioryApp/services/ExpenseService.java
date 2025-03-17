package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.ExpenseRequestDTO;
import com.imperial_net.inventioryApp.dto.ExpenseResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ExpenseException;
import com.imperial_net.inventioryApp.models.Expense;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.ExpenseRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j  // Para logs
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CookieService cookieService;

    /**
     * Registra un gasto en la base de datos.
     */
    public ExpenseResponseDTO registerExpense(ExpenseRequestDTO expenseDto, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ExpenseException("Usuario no autenticado. No se puede registrar el gasto."));

        Expense expense = convertToEntity(expenseDto);
        expense.setCreatedBy(user);

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDto(savedExpense);
    }

    /**
     * Obtiene todos los gastos registrados por un usuario autenticado.
     */
    public List<ExpenseResponseDTO> getAllExpensesForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ExpenseException("Usuario no autenticado"));

        return expenseRepository.findAllByCreatedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Actualiza un gasto existente en la base de datos.
     */
    public void updateExpense(Long id, ExpenseRequestDTO expenseRequest, HttpServletRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseException("Gasto no encontrado en la base de datos"));

        cookieService.getUserFromCookie(request); // Validar autenticación

        log.info("Actualizando gasto con ID: {}", id);
        log.info("Fecha recibida en DTO: {}", expenseRequest.getExpenseDate());

        updateEntity(expense, expenseRequest);
        expenseRepository.save(expense);
    }

    /**
     * Elimina un gasto de la base de datos.
     */
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseException("El gasto no existe en la base de datos.");
        }
        expenseRepository.deleteById(id);
    }

    /**
     * Obtiene los gastos dentro de un rango de fechas asegurando que `endDate` no incluya un día extra.
     */
    public List<ExpenseResponseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);
        return expenses.stream().map(this::convertToDto).toList();
    }

    /**
     * Convierte un `Expense` en un `ExpenseResponseDTO`.
     */
    public ExpenseResponseDTO convertToDto(Expense expense) {
        if (expense == null) {
            throw new ExpenseException("El gasto no puede ser nulo");
        }

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getDate() != null ? expense.getDate() : LocalDate.now(), // Evita null en la fecha
                expense.getExpenseType(),
                expense.getAmount(),
                expense.getPaymentMethod(),
                expense.getDescription(),
                (expense.getCreatedBy() != null) ?
                        (expense.getCreatedBy().getFirstName() + " " + expense.getCreatedBy().getLastName()) : "Desconocido"
        );
    }

    /**
     * Convierte un `ExpenseRequestDTO` en un `Expense`.
     */
    public Expense convertToEntity(ExpenseRequestDTO dto) {
        Expense expense = new Expense();

        LocalDate fechaGasto = dto.getExpenseDate();
        if (fechaGasto == null) {
            fechaGasto = LocalDate.now();
            log.warn("Fecha no proporcionada en DTO, asignando LocalDate.now()");
        }

        expense.setDate(fechaGasto);
        expense.setExpenseType(dto.getExpenseType());
        expense.setAmount(dto.getAmount());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setDescription(dto.getDescription());

        log.info("Fecha final asignada al gasto: {}", expense.getDate());
        return expense;
    }

    /**
     * Actualiza una entidad `Expense` con los valores de un `ExpenseRequestDTO`.
     */
    private void updateEntity(Expense expense, ExpenseRequestDTO dto) {
        if (dto.getExpenseDate() != null) {
            expense.setDate(dto.getExpenseDate());
        } else {
            log.warn("La fecha no fue proporcionada en la actualización, se mantiene la existente: {}", expense.getDate());
        }

        expense.setExpenseType(dto.getExpenseType());
        expense.setAmount(dto.getAmount());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setDescription(dto.getDescription());
    }

    /**
     * Obtiene los gastos registrados en una fecha exacta.
     */
    public List<ExpenseResponseDTO> getExpensesByExactDate(LocalDate date) {
        List<Expense> expenses = expenseRepository.findByDate(date);
        return expenses.stream().map(this::convertToDto).toList();
    }
}
