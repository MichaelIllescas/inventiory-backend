package com.imperial_net.inventioryApp.reports.controller;

import com.imperial_net.inventioryApp.reports.dto.DailyIncomeResponse;
import com.imperial_net.inventioryApp.reports.dto.ProfitabilityDTO;
import com.imperial_net.inventioryApp.reports.dto.TopCustomerResponse;
import com.imperial_net.inventioryApp.reports.dto.TopSellingProductResponse;
import com.imperial_net.inventioryApp.reports.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Controlador para generar reportes financieros y estadísticos.
 * Proporciona endpoints para ingresos diarios, mensuales, anuales, clientes destacados, productos más vendidos y rentabilidad.
 */
@RestController
@RequestMapping("/reports")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;

    /**
     * Devuelve los ingresos totales del día seleccionado.
     *
     * @param date    Fecha en formato ISO (yyyy-MM-dd).
     * @param request Solicitud HTTP.
     * @return ingresos del día.
     */
    @GetMapping("/daily-income")
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(@RequestParam String date, HttpServletRequest request) {
        LocalDate selectedDate = LocalDate.parse(date);
        return ResponseEntity.ok(reportService.getDailyIncome(selectedDate, request));
    }

    /**
     * Devuelve los ingresos del mes seleccionado.
     *
     * @param month   Mes en formato yyyy-MM.
     * @param request Solicitud HTTP.
     * @return ingresos del mes.
     */
    @GetMapping("/monthly-income")
    public ResponseEntity<DailyIncomeResponse> getMonthlyIncome(@RequestParam String month, HttpServletRequest request) {
        YearMonth selectedMonth = YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getMonthlyIncome(selectedMonth, request));
    }

    /**
     * Devuelve los ingresos acumulados del año seleccionado.
     *
     * @param year    Año (ej. 2024).
     * @param request Solicitud HTTP.
     * @return ingresos del año.
     */
    @GetMapping("/annual-income")
    public ResponseEntity<DailyIncomeResponse> getAnnualIncome(@RequestParam Integer year, HttpServletRequest request) {
        return ResponseEntity.ok(reportService.getAnnualIncome(year, request));
    }

    /**
     * Devuelve una lista de los clientes con mayor volumen de compras en un mes específico.
     *
     * @param month   Mes en formato yyyy-MM.
     * @param request Solicitud HTTP.
     * @return lista de clientes destacados.
     */
    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers(@RequestParam String month, HttpServletRequest request) {
        YearMonth selectedMonth = YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getTopCustomersForMonth(selectedMonth, request));
    }

    /**
     * Devuelve una lista de los productos más vendidos en un mes específico.
     *
     * @param month   Mes en formato yyyy-MM.
     * @param request Solicitud HTTP.
     * @return lista de productos más vendidos.
     */
    @GetMapping("/top-selling-products")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts(@RequestParam String month, HttpServletRequest request) {
        YearMonth selectedMonth = YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getTopSellingProducts(selectedMonth, request));
    }

    /**
     * Devuelve la rentabilidad total del negocio para el año seleccionado.
     *
     * @param year    Año de consulta.
     * @param request Solicitud HTTP.
     * @return rentabilidad porcentual.
     */
    @GetMapping("/profitability")
    public ResponseEntity<ProfitabilityDTO> getProfitability(@RequestParam Integer year, HttpServletRequest request) {
        return ResponseEntity.ok(reportService.getProfitabilityByYear(year, request));
    }
}
