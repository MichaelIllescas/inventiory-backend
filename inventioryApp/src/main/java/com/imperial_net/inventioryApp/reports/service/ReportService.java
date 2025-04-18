package com.imperial_net.inventioryApp.reports.service;

import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.expenses.model.Expense;
import com.imperial_net.inventioryApp.expenses.repository.ExpenseRepository;
import com.imperial_net.inventioryApp.reports.dto.*;
import com.imperial_net.inventioryApp.sales.model.Sale;
import com.imperial_net.inventioryApp.sales.model.SaleDetail;
import com.imperial_net.inventioryApp.sales.repository.SaleDetailRepository;
import com.imperial_net.inventioryApp.sales.repository.SaleRepository;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de generar los reportes del sistema.
 * Proporciona métodos para calcular ingresos, ganancias, rentabilidad, etc.
 */
@RequiredArgsConstructor
@Service
public class ReportService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ExpenseRepository expenseRepository;
    private final CookieService cookieService;

    /**
     * Calcula los ingresos y ganancias de un día específico.
     */
    public DailyIncomeResponse getDailyIncome(LocalDate date, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        return calculateIncome(date, date, user.getId());
    }

    /**
     * Calcula los ingresos y ganancias de un mes específico.
     */
    public DailyIncomeResponse getMonthlyIncome(YearMonth month, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        return calculateIncome(month.atDay(1), month.atEndOfMonth(), user.getId());
    }

    /**
     * Calcula los ingresos y ganancias de un año específico.
     */
    public DailyIncomeResponse getAnnualIncome(int year, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        return calculateIncome(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31), user.getId());
    }

    /**
     * Calcula ingresos, costos y ganancias en un rango de fechas.
     */
    private DailyIncomeResponse calculateIncome(LocalDate startDate, LocalDate endDate, Long userId) {
        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate, userId);

        BigDecimal grossIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Sale sale : sales) {
            grossIncome = grossIncome.add(sale.getTotalSale());
            for (SaleDetail detail : sale.getSaleDetails()) {
                BigDecimal costForDetail = detail.getCostPrice().multiply(detail.getQuantity());
                totalCost = totalCost.add(costForDetail);
            }
        }

        BigDecimal grossProfit = grossIncome.subtract(totalCost);
        return new DailyIncomeResponse(grossIncome, totalCost, grossProfit);
    }

    /**
     * Obtiene los clientes que más compraron en el mes dado.
     */
    public List<TopCustomerResponse> getTopCustomersForMonth(YearMonth month, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Object[]> results = saleRepository.findTopCustomersForMonth(startDate, endDate, user.getId());

        return results.stream()
                .map(row -> new TopCustomerResponse(
                        (String) row[0] + " " + (String) row[1], // Nombre completo
                        (String) row[2], // DNI
                        (BigDecimal) row[3], // Total gastado
                        ((Number) row[4]).longValue() // Cantidad de compras
                ))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los 10 productos más vendidos en un mes.
     */
    public List<TopSellingProductResponse> getTopSellingProducts(YearMonth month, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Object[]> results = saleDetailRepository.findTop10SellingProducts(startDate, endDate, user.getId());

        return results.stream()
                .map(row -> new TopSellingProductResponse(
                        (String) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        (BigDecimal) row[3]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Calcula la rentabilidad anual del negocio.
     */
    public ProfitabilityDTO getProfitabilityByYear(int year, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate, user.getId());
        List<Expense> expenses = expenseRepository.findExpensesBetweenDates(startDate, endDate, user.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalProductCost = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Sale sale : sales) {
            totalIncome = totalIncome.add(sale.getTotalSale());
            totalProductCost = totalProductCost.add(sale.getTotalCost());
        }

        for (Expense expense : expenses) {
            totalExpenses = totalExpenses.add(BigDecimal.valueOf(expense.getAmount()));
        }

        BigDecimal totalCosts = totalProductCost.add(totalExpenses);
        BigDecimal netProfit = totalIncome.subtract(totalCosts);
        BigDecimal profitabilityPercentage = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        List<QuarterlyProfitDTO> quarterlyData = generateQuarterlyData(year, request);

        return new ProfitabilityDTO(year, totalIncome, totalProductCost, totalExpenses, netProfit, profitabilityPercentage, quarterlyData);
    }

    /**
     * Genera los datos de rentabilidad por trimestre para un año.
     */
    private List<QuarterlyProfitDTO> generateQuarterlyData(int year, HttpServletRequest request) {
        User user = getUserFromCookie(request);
        List<QuarterlyProfitDTO> quarterlyData = new ArrayList<>();

        for (int quarter = 1; quarter <= 4; quarter++) {
            LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
            LocalDate end = start.plusMonths(2).withDayOfMonth(start.plusMonths(2).lengthOfMonth());

            List<Sale> quarterlySales = saleRepository.findSalesBetweenDates(start, end, user.getId());
            List<Expense> quarterlyExpenses = expenseRepository.findExpensesBetweenDates(start, end, user.getId());

            BigDecimal quarterIncome = BigDecimal.ZERO;
            BigDecimal quarterProductCost = BigDecimal.ZERO;
            BigDecimal quarterExpenses = BigDecimal.ZERO;

            for (Sale sale : quarterlySales) {
                quarterIncome = quarterIncome.add(sale.getTotalSale());
                quarterProductCost = quarterProductCost.add(sale.getTotalCost());
            }

            for (Expense expense : quarterlyExpenses) {
                quarterExpenses = quarterExpenses.add(BigDecimal.valueOf(expense.getAmount()));
            }

            BigDecimal quarterTotalCosts = quarterProductCost.add(quarterExpenses);
            BigDecimal quarterNetProfit = quarterIncome.subtract(quarterTotalCosts);
            BigDecimal quarterPercentage = quarterIncome.compareTo(BigDecimal.ZERO) > 0
                    ? quarterNetProfit.divide(quarterIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            quarterlyData.add(new QuarterlyProfitDTO("Trimestre " + quarter, quarterIncome, quarterProductCost, quarterExpenses, quarterNetProfit, quarterPercentage));
        }

        return quarterlyData;
    }

    /**
     * Obtiene el usuario autenticado desde la cookie.
     */
    private User getUserFromCookie(HttpServletRequest request) {
        return cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede procesar la solicitud."));
    }

    /**
     * Retorna los ingresos del día actual.
     */
    public DailyIncomeResponse getTodayIncome(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        return getDailyIncome(today, request);
    }

    /**
     * Calcula los ingresos del trimestre actual.
     */
    public DailyIncomeResponse getCurrentQuarterIncome(HttpServletRequest request) {
        User user = getUserFromCookie(request);
        LocalDate today = LocalDate.now();

        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int startMonth = ((currentMonth - 1) / 3) * 3 + 1;

        LocalDate startDate = LocalDate.of(currentYear, startMonth, 1);
        LocalDate endDate = startDate.plusMonths(2).withDayOfMonth(startDate.plusMonths(2).lengthOfMonth());

        return calculateIncome(startDate, endDate, user.getId());
    }

    /**
     * Devuelve los clientes más destacados del mes actual.
     */
    public List<TopCustomerResponse> getTopCustomersOfCurrentMonth(HttpServletRequest request) {
        YearMonth currentMonth = YearMonth.now();
        return getTopCustomersForMonth(currentMonth, request);
    }
}
