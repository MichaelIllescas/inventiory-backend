package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.models.*;
import com.imperial_net.inventioryApp.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboarService {

    private  final CookieService cookieService;
    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductService productService;
    private final ReportService reportService;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;



    //total de gancias
    public BigDecimal getTotalNetProfit(HttpServletRequest request) {
        User user = getUserFromCookie(request); // Usá tu cookieService acá también

        List<Sale> allSales = saleRepository.findByUser(user);
        List<Expense> allExpenses = expenseRepository.findAllByCreatedBy_Id(user.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Sale sale : allSales) {
            totalIncome = totalIncome.add(sale.getTotalSale());
            totalCost = totalCost.add(sale.getTotalCost());
        }

        for (Expense expense : allExpenses) {
            totalExpenses = totalExpenses.add(BigDecimal.valueOf(expense.getAmount()));
        }

        BigDecimal totalCosts = totalCost.add(totalExpenses);
        return totalIncome.subtract(totalCosts);
    }

    //ganancias del mes
    public BigDecimal getCurrentMonthProfit(HttpServletRequest request) {
        User user = getUserFromCookie(request); // Reutilizá tu método existente

        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

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
        return totalIncome.subtract(totalCosts); // Ganancia neta del mes actual
    }

    //rentabilidad del negocio
    public BigDecimal getTotalProfitability(HttpServletRequest request) {
        User user = getUserFromCookie(request);

        List<Sale> allSales = saleRepository.findByUser(user);
        List<Expense> allExpenses = expenseRepository.findAllByCreatedBy_Id(user.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalProductCost = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Sale sale : allSales) {
            totalIncome = totalIncome.add(sale.getTotalSale());
            totalProductCost = totalProductCost.add(sale.getTotalCost());
        }

        for (Expense expense : allExpenses) {
            totalExpenses = totalExpenses.add(BigDecimal.valueOf(expense.getAmount()));
        }

        BigDecimal totalCosts = totalProductCost.add(totalExpenses);
        BigDecimal netProfit = totalIncome.subtract(totalCosts);

        if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // Evitar división por cero
        }

        // Rentabilidad en porcentaje con 2 decimales
        return netProfit.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // diez priductos mas vendidos
    public List<TopSellingProductResponse> getTop10MostSoldProducts(HttpServletRequest request) {
        User user = getUserFromCookie(request);

        // Podés usar fechas amplias para tomar todo el historial
        LocalDate startDate = LocalDate.of(2000, 1, 1); // Fecha simbólica de inicio
        LocalDate endDate = LocalDate.now();

        List<Object[]> results = saleDetailRepository.findTop10SellingProducts(startDate, endDate, user.getId());

        return results.stream()
                .map(row -> new TopSellingProductResponse(
                        (String) row[0], // Nombre del producto
                        (String) row[1], // Código del producto
                        ((Number) row[2]).longValue(), // Cantidad total vendida
                        (BigDecimal) row[3] // Total generado por ese producto
                ))
                .collect(Collectors.toList());
    }

    //productos con stock bajo
    public List<StockLowDTO> getLowStockProducts(HttpServletRequest request) {
        return productService.findLowStockProducts(request);
    }

    //ganancias de los ultimos 7 dias
    public List<DailyProfitDTO> getLast7DaysProfits(HttpServletRequest request) {
        User user = getUserFromCookie(request);
        List<DailyProfitDTO> profits = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            DailyIncomeResponse income = reportService.getDailyIncome(date, request);
            profits.add(new DailyProfitDTO(date, income.getGrossProfit()));
        }

        return profits;
    }


    // ganancias del trimestre
        public DailyIncomeResponse getCurrentQuarterProfit(HttpServletRequest request) {
            return reportService.getCurrentQuarterIncome(request);
        }

        // top de clientes
        public List<TopCustomerResponse> getTopClientsThisMonth(HttpServletRequest request) {
            return reportService.getTopCustomersOfCurrentMonth(request);
        }

        //total de clientes del usuario
    public Long getTotalRegisteredClients(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return clientRepository.countClientsByUserId(userId);
    }

//total de productos registardos:
public Long getTotalRegisteredProducts(HttpServletRequest request) {
    Long userId = cookieService.getUserFromCookie(request)
            .map(User::getId)
            .orElseThrow(() -> new ProductException("Usuario no autenticado"));

    return productRepository.countByRegistratedBy_Id(userId);
}

//total de capital invertido actual
    public BigDecimal getTotalInvestedCapital(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        List<Purchase> purchases = purchaseRepository.findActivePurchasesWithStockByUser(userId);

        BigDecimal totalInvestment = BigDecimal.ZERO;

        for (Purchase purchase : purchases) {
            BigDecimal investment = purchase.getPurchasePrice()
                    .multiply(purchase.getRemainingStock());
            totalInvestment = totalInvestment.add(investment);
        }

        return totalInvestment;
    }

    //ganancias del dia
    public DailyIncomeResponse getTodayProfit(HttpServletRequest request) {
        return reportService.getTodayIncome(request);}

    public DashboardResponseDTO getDashboardData(HttpServletRequest request) {
        return new DashboardResponseDTO(
                getTotalNetProfit(request),
                getCurrentMonthProfit(request),
                getTotalProfitability(request),
                getTodayProfit(request).getGrossProfit(),
                getCurrentQuarterMonthlyProfits(request).stream()
                        .map(QuarterlyMonthProfitDTO::getProfit)
                        .reduce(BigDecimal.ZERO, BigDecimal::add), // ✅ AHORA ESTÁ CORRECTO
                getTotalInvestedCapital(request),
                getTotalRegisteredClients(request),
                getTotalRegisteredProducts(request),
                getTop10MostSoldProducts(request),
                getLowStockProducts(request),
                getTopClientsThisMonth(request),
                getLast7DaysProfits(request),
                getCurrentQuarterMonthlyProfits(request)
        );
    }

    public List<QuarterlyMonthProfitDTO> getCurrentQuarterMonthlyProfits(HttpServletRequest request) {
        User user = getUserFromCookie(request);
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();

        // Determinar el primer mes del trimestre
        int quarterStartMonth = ((currentMonth - 1) / 3) * 3 + 1;

        List<QuarterlyMonthProfitDTO> monthlyProfits = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int month = quarterStartMonth + i;
            LocalDate start = LocalDate.of(now.getYear(), month, 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

            List<Sale> sales = saleRepository.findSalesBetweenDates(start, end, user.getId());
            List<Expense> expenses = expenseRepository.findExpensesBetweenDates(start, end, user.getId());

            BigDecimal income = BigDecimal.ZERO;
            BigDecimal productCost = BigDecimal.ZERO;
            BigDecimal monthlyExpenses = BigDecimal.ZERO;

            for (Sale sale : sales) {
                income = income.add(sale.getTotalSale());
                for (SaleDetail detail : sale.getSaleDetails()) {
                    BigDecimal costForDetail = detail.getCostPrice().multiply(detail.getQuantity());
                    productCost = productCost.add(costForDetail);
                }
            }

            for (Expense expense : expenses) {
                monthlyExpenses = monthlyExpenses.add(BigDecimal.valueOf(expense.getAmount()));
            }

            BigDecimal totalCost = productCost.add(monthlyExpenses);
            BigDecimal netProfit = income.subtract(totalCost);

            monthlyProfits.add(new QuarterlyMonthProfitDTO(month, netProfit));
        }

        return monthlyProfits;
    }



    private User getUserFromCookie(HttpServletRequest request) {
        return cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede procesar la solicitud."));
    }

}
