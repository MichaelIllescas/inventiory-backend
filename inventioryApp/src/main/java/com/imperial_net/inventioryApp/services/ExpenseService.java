package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.ExpenseRequestDTO;
import com.imperial_net.inventioryApp.dto.ExpenseResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ExpenseException;
import com.imperial_net.inventioryApp.models.Expense;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.ExpenseRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CookieService cookieService;

    public ExpenseResponseDTO registerExpense(ExpenseRequestDTO expenseDto, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ExpenseException("Usuario no autenticado. No se puede registrar el gasto."));

        Expense expense = convertToEntity(expenseDto);
        expense.setCreatedBy(user);
        expense.setDate(LocalDate.now()); // Se asigna la fecha automáticamente

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDto(savedExpense);
    }

    public List<ExpenseResponseDTO> getAllExpensesForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ExpenseException("Usuario no autenticado"));

        return expenseRepository.findAllByCreatedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public void updateExpense(Long id, ExpenseRequestDTO expenseRequest, HttpServletRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseException("Gasto no encontrado en la base de datos"));

        cookieService.getUserFromCookie(request); // Validar autenticación sin restricciones

        updateEntity(expense, expenseRequest);
        expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseException("El gasto no existe en la base de datos.");
        }
        expenseRepository.deleteById(id);
    }

    public ExpenseResponseDTO convertToDto(Expense expense) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setId(expense.getId());
        dto.setDate(expense.getDate());
        dto.setExpenseType(expense.getExpenseType());
        dto.setAmount(expense.getAmount());
        dto.setPaymentMethod(expense.getPaymentMethod());
        dto.setDescription(expense.getDescription());
        dto.setCreatedBy(expense.getCreatedBy() != null ? expense.getCreatedBy().getFirstName() + " " + expense.getCreatedBy().getLastName() : null);
        return dto;
    }

    public Expense convertToEntity(ExpenseRequestDTO dto) {
        Expense expense = new Expense();
        expense.setExpenseType(dto.getExpenseType());
        expense.setAmount(dto.getAmount());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setDescription(dto.getDescription());
        return expense;
    }

    private void updateEntity(Expense expense, ExpenseRequestDTO dto) {
        expense.setExpenseType(dto.getExpenseType());
        expense.setAmount(dto.getAmount());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setDescription(dto.getDescription());
    }
}
