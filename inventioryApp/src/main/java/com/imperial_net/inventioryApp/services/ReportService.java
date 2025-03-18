package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.models.Expense;
import com.imperial_net.inventioryApp.models.Sale;
import com.imperial_net.inventioryApp.models.SaleDetail;
import com.imperial_net.inventioryApp.models.Purchase;
import com.imperial_net.inventioryApp.repositories.ExpenseRepository;
import com.imperial_net.inventioryApp.repositories.SaleDetailRepository;
import com.imperial_net.inventioryApp.repositories.SaleRepository;
import com.imperial_net.inventioryApp.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Objects; // Importar para validaciones
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ExpenseRepository expenseRepository;



    public DailyIncomeResponse getDailyIncome(LocalDate date) {
        List<Sale> sales = saleRepository.findSalesByDate(date);

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

    public DailyIncomeResponse getMonthlyIncome(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);

        BigDecimal grossIncome = BigDecimal.ZERO; // Ingresos brutos (total de ventas)
        BigDecimal totalCost = BigDecimal.ZERO;   // Costo total de los productos vendidos

        for (Sale sale : sales) {
            grossIncome = grossIncome.add(sale.getTotalSale()); // ✅ Total después de descuento

            // Sumamos los costos de cada producto vendido en la venta
            for (SaleDetail detail : sale.getSaleDetails()) {
                BigDecimal costForDetail = detail.getCostPrice().multiply(detail.getQuantity());
                totalCost = totalCost.add(costForDetail);
            }
        }

        // ✅ Ganancia Bruta (Ingresos - Costos)
        BigDecimal grossProfit = grossIncome.subtract(totalCost);

        return new DailyIncomeResponse(grossIncome, totalCost, grossProfit);
    }

    public DailyIncomeResponse getAnnualIncome(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);

        BigDecimal grossIncome = BigDecimal.ZERO; // Ingresos brutos (total de ventas)
        BigDecimal totalCost = BigDecimal.ZERO;   // Costo total de los productos vendidos

        for (Sale sale : sales) {
            grossIncome = grossIncome.add(sale.getTotalSale()); // ✅ Total después de descuento

            // Sumamos los costos de cada producto vendido en la venta
            for (SaleDetail detail : sale.getSaleDetails()) {
                BigDecimal costForDetail = detail.getCostPrice().multiply(detail.getQuantity());
                totalCost = totalCost.add(costForDetail);
            }
        }

        // ✅ Ganancia Bruta (Ingresos - Costos)
        BigDecimal grossProfit = grossIncome.subtract(totalCost);

        return new DailyIncomeResponse(grossIncome, totalCost, grossProfit);
    }

    public List<TopCustomerResponse> getTopCustomersForMonth(YearMonth month) {
        LocalDate startDate = month.atDay(1);      // Primer día del mes
        LocalDate endDate = month.atEndOfMonth();  // Último día del mes

        List<Object[]> results = saleRepository.findTopCustomersForMonth(startDate, endDate);

        return results.stream()
                .map(row -> new TopCustomerResponse(
                        (String) row[0] + ' ' + (String) row[1], // Nombre del cliente
                        (String) row[2], // Número de documento
                        (BigDecimal) row[3], // Total gastado
                        ((Number) row[4]).longValue() // Total de compras realizadas
                ))
                .collect(Collectors.toList());
    }

    public List<TopSellingProductResponse> getTopSellingProducts(YearMonth month) {
        LocalDate startDate = month.atDay(1); // Primer día del mes
        LocalDate endDate = month.atEndOfMonth(); // Último día del mes

        List<Object[]> results = saleDetailRepository.findTop10SellingProducts(startDate, endDate);

        return results.stream()
                .map(row -> new TopSellingProductResponse(
                        (String) row[0], // Nombre del producto
                        (String) row[1], // Código del producto
                        ((Number) row[2]).longValue(), // Cantidad total vendida
                        (BigDecimal) row[3] // Total generado
                ))
                .collect(Collectors.toList());
    }

    public ProfitabilityDTO getProfitabilityByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);
        List<Expense> expenses = expenseRepository.findExpensesBetweenDates(startDate, endDate);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Sale sale : sales) {
            income = income.add(sale.getTotalSale());
            totalCost = totalCost.add(sale.getTotalCost());
        }

        for (Expense expense : expenses) {
            totalExpenses = totalExpenses.add(BigDecimal.valueOf(expense.getAmount()));
        }

        BigDecimal netProfit = income.subtract(totalCost).subtract(totalExpenses);
        BigDecimal profitabilityPercentage = income.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(income, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        List<QuarterlyProfitDTO> quarterlyData = generateQuarterlyData(year);

        return new ProfitabilityDTO(year, income, totalCost, totalExpenses, netProfit, profitabilityPercentage, quarterlyData);
    }

    public ProfitabilityDTO getProfitabilityByYear(Integer year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);
        List<Expense> expenses = expenseRepository.findExpensesBetweenDates(startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalProductCost = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Sale sale : sales) {
            totalIncome = totalIncome.add(sale.getTotalSale());
            totalProductCost = totalProductCost.add(sale.getTotalCost()); // Costo de productos vendidos
        }

        for (Expense expense : expenses) {
            totalExpenses = totalExpenses.add(BigDecimal.valueOf(expense.getAmount())); // Gastos operativos
        }

        BigDecimal totalCosts = totalProductCost.add(totalExpenses); // Suma de costos de productos + gastos operativos
        BigDecimal netProfit = totalIncome.subtract(totalCosts);
        BigDecimal profitabilityPercentage = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        List<QuarterlyProfitDTO> quarterlyData = generateQuarterlyData(year);

        return new ProfitabilityDTO(year, totalIncome, totalProductCost, totalExpenses, netProfit, profitabilityPercentage, quarterlyData);
    }

    private List<QuarterlyProfitDTO> generateQuarterlyData(int year) {
        List<QuarterlyProfitDTO> quarterlyData = new ArrayList<>();

        for (int quarter = 1; quarter <= 4; quarter++) {
            LocalDate start = LocalDate.of(year, (quarter - 1) * 3 + 1, 1);
            LocalDate end = start.plusMonths(2).withDayOfMonth(start.plusMonths(2).lengthOfMonth());

            List<Sale> quarterlySales = saleRepository.findSalesBetweenDates(start, end);
            List<Expense> quarterlyExpenses = expenseRepository.findExpensesBetweenDates(start, end);

            BigDecimal quarterIncome = BigDecimal.ZERO;
            BigDecimal quarterProductCost = BigDecimal.ZERO;
            BigDecimal quarterExpenses = BigDecimal.ZERO;

            for (Sale sale : quarterlySales) {
                quarterIncome = quarterIncome.add(sale.getTotalSale());
                quarterProductCost = quarterProductCost.add(sale.getTotalCost()); // Costo de productos vendidos
            }

            for (Expense expense : quarterlyExpenses) {
                quarterExpenses = quarterExpenses.add(BigDecimal.valueOf(expense.getAmount())); // Gastos operativos
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
}
