package com.imperial_net.inventioryApp.expenses.service;

import com.imperial_net.inventioryApp.expenses.dto.ExpenseRequestDTO;
import com.imperial_net.inventioryApp.expenses.dto.ExpenseResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ExpenseException;
import com.imperial_net.inventioryApp.expenses.model.Expense;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.expenses.repository.ExpenseRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio encargado de la lógica de negocio relacionada con los gastos.
 * Permite registrar, actualizar, eliminar y consultar gastos por usuario y fechas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CookieService cookieService;

    /**
     * Registra un gasto en la base de datos para el usuario autenticado.
     *
     * @param expenseDto DTO con la información del gasto.
     * @param request    solicitud HTTP con la cookie del usuario.
     * @return el gasto guardado como DTO de respuesta.
     * @throws ExpenseException si el usuario no está autenticado o excede el límite del plan FREE.
     */
    public ExpenseResponseDTO registerExpense(ExpenseRequestDTO expenseDto, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ExpenseException("Usuario no autenticado. No se puede registrar el gasto."));

        Expense expense = convertToEntity(expenseDto);
        expense.setCreatedBy(user);

        if (this.validateNumberOfRecords(user)) {
            Expense savedExpense = expenseRepository.save(expense);
            return convertToDto(savedExpense);
        } else {
            throw new ExpenseException("Ha alcanzado el límite de registros para el plan FREE. "
                    + "Si desea acceder a registros ilimitados, debe suscribirse al plan PRO.");
        }
    }

    /**
     * Devuelve todos los gastos del usuario autenticado.
     *
     * @param request solicitud HTTP con la cookie del usuario.
     * @return lista de gastos en formato DTO.
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
     * Actualiza un gasto existente.
     *
     * @param id              ID del gasto a actualizar.
     * @param expenseRequest  nuevos datos del gasto.
     * @param request         solicitud HTTP para validar autenticación.
     */
    public void updateExpense(Long id, ExpenseRequestDTO expenseRequest, HttpServletRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseException("Gasto no encontrado en la base de datos"));

        cookieService.getUserFromCookie(request); // Solo validación de autenticación

        log.info("Actualizando gasto con ID: {}", id);
        log.info("Fecha recibida en DTO: {}", expenseRequest.getExpenseDate());

        updateEntity(expense, expenseRequest);
        expenseRepository.save(expense);
    }

    /**
     * Elimina un gasto por su ID.
     *
     * @param id identificador del gasto.
     * @throws ExpenseException si el gasto no existe.
     */
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ExpenseException("El gasto no existe en la base de datos.");
        }
        expenseRepository.deleteById(id);
    }

    /**
     * Obtiene los gastos dentro de un rango de fechas (sin filtrar por usuario).
     *
     * @param startDate fecha de inicio.
     * @param endDate   fecha de fin.
     * @return lista de gastos convertidos a DTO.
     */
    public List<ExpenseResponseDTO> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = expenseRepository.findByDateBetween(startDate, endDate);
        return expenses.stream().map(this::convertToDto).toList();
    }

    /**
     * Obtiene los gastos realizados en una fecha exacta.
     *
     * @param date fecha a filtrar.
     * @return lista de gastos en formato DTO.
     */
    public List<ExpenseResponseDTO> getExpensesByExactDate(LocalDate date) {
        List<Expense> expenses = expenseRepository.findByDate(date);
        return expenses.stream().map(this::convertToDto).toList();
    }

    /**
     * Convierte una entidad {@link Expense} en un DTO {@link ExpenseResponseDTO}.
     *
     * @param expense entidad de gasto.
     * @return DTO de respuesta.
     * @throws ExpenseException si el gasto es nulo.
     */
    public ExpenseResponseDTO convertToDto(Expense expense) {
        if (expense == null) {
            throw new ExpenseException("El gasto no puede ser nulo");
        }

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getDate() != null ? expense.getDate() : LocalDate.now(),
                expense.getExpenseType(),
                expense.getAmount(),
                expense.getPaymentMethod(),
                expense.getDescription(),
                (expense.getCreatedBy() != null)
                        ? expense.getCreatedBy().getFirstName() + " " + expense.getCreatedBy().getLastName()
                        : "Desconocido"
        );
    }

    /**
     * Convierte un DTO {@link ExpenseRequestDTO} en una entidad {@link Expense}.
     *
     * @param dto datos recibidos desde el cliente.
     * @return entidad de gasto lista para persistir.
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
     * Actualiza una entidad {@link Expense} con los datos de un {@link ExpenseRequestDTO}.
     *
     * @param expense entidad existente a modificar.
     * @param dto     nuevos datos a aplicar.
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
     * Valida si el usuario tiene permitido registrar más gastos según su plan de suscripción.
     *
     * @param user usuario autenticado.
     * @return true si puede registrar más gastos; false si llegó al límite.
     */
    public boolean validateNumberOfRecords(User user) {
        if (user.getSubscription() == Subscription.FREE) {
            return expenseRepository.countByCreatedBy_Id(user.getId()) < 10;
        }
        return true;
    }
}
