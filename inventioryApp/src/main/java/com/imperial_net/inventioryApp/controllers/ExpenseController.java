package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.ExpenseRequestDTO;
import com.imperial_net.inventioryApp.dto.ExpenseResponseDTO;
import com.imperial_net.inventioryApp.services.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping("/register")
    public ResponseEntity<ExpenseResponseDTO> registerExpense(
            @Valid @RequestBody ExpenseRequestDTO expenseDto, HttpServletRequest request) {

        ExpenseResponseDTO savedExpense = expenseService.registerExpense(expenseDto, request);
        return ResponseEntity.ok(savedExpense);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getExpenses(HttpServletRequest request) {
        return ResponseEntity.ok(expenseService.getAllExpensesForUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id, @RequestBody ExpenseRequestDTO expenseRequest, HttpServletRequest request) {
        expenseService.updateExpense(id, expenseRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body("Gasto actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Gasto eliminado correctamente");
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String exactDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Convertimos las fechas de `String` a `LocalDate`
        LocalDate start = (startDate != null) ? LocalDate.parse(startDate, formatter) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate, formatter) : null;
        LocalDate exact = (exactDate != null) ? LocalDate.parse(exactDate, formatter) : null;

        if (exact != null) {
            return ResponseEntity.ok(expenseService.getExpensesByExactDate(exact));
        } else if (start != null && end != null) {
            return ResponseEntity.ok(expenseService.getExpensesByDateRange(start, end));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
