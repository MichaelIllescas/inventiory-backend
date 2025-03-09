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
}
