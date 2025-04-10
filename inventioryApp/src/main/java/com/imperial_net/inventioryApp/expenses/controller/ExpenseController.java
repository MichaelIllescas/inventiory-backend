package com.imperial_net.inventioryApp.expenses.controller;

import com.imperial_net.inventioryApp.expenses.dto.ExpenseRequestDTO;
import com.imperial_net.inventioryApp.expenses.dto.ExpenseResponseDTO;
import com.imperial_net.inventioryApp.expenses.service.ExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador REST para la gestión de gastos.
 * Proporciona endpoints para registrar, actualizar, eliminar y filtrar gastos.
 */
@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Registra un nuevo gasto.
     *
     * @param expenseDto datos del gasto a registrar.
     * @param request    solicitud HTTP que contiene información del usuario.
     * @return el gasto registrado.
     */
    @PostMapping("/register")
    public ResponseEntity<ExpenseResponseDTO> registerExpense(
            @Valid @RequestBody ExpenseRequestDTO expenseDto,
            HttpServletRequest request) {

        ExpenseResponseDTO savedExpense = expenseService.registerExpense(expenseDto, request);
        return ResponseEntity.ok(savedExpense);
    }

    /**
     * Obtiene todos los gastos registrados por el usuario autenticado.
     *
     * @param request solicitud HTTP con la sesión del usuario.
     * @return lista de gastos del usuario.
     */
    @GetMapping("/getAll")
    public ResponseEntity<?> getExpenses(HttpServletRequest request) {
        return ResponseEntity.ok(expenseService.getAllExpensesForUser(request));
    }

    /**
     * Actualiza un gasto existente por su ID.
     *
     * @param id              identificador del gasto.
     * @param expenseRequest  nuevos datos del gasto.
     * @param request         solicitud HTTP con información del usuario.
     * @return mensaje de éxito.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseRequestDTO expenseRequest,
            HttpServletRequest request) {

        expenseService.updateExpense(id, expenseRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body("Gasto actualizado correctamente");
    }

    /**
     * Elimina un gasto por su ID.
     *
     * @param id identificador del gasto a eliminar.
     * @return mensaje de éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok("Gasto eliminado correctamente");
    }

    /**
     * Filtra los gastos por un rango de fechas o una fecha exacta.
     *
     * @param startDate  fecha de inicio (formato dd/MM/yyyy).
     * @param endDate    fecha de fin (formato dd/MM/yyyy).
     * @param exactDate  fecha exacta (formato dd/MM/yyyy).
     * @return lista de gastos filtrados según las fechas indicadas.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String exactDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
