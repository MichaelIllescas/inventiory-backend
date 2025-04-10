package com.imperial_net.inventioryApp.dashboard.service;

import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.clients.repository.ClientRepository;
import com.imperial_net.inventioryApp.dashboard.dto.DashboardResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.expenses.model.Expense;
import com.imperial_net.inventioryApp.expenses.repository.ExpenseRepository;
import com.imperial_net.inventioryApp.products.dto.StockLowDTO;
import com.imperial_net.inventioryApp.products.repository.ProductRepository;
import com.imperial_net.inventioryApp.products.service.ProductService;
import com.imperial_net.inventioryApp.purchases.model.Purchase;
import com.imperial_net.inventioryApp.purchases.repository.PurchaseRepository;
import com.imperial_net.inventioryApp.reports.dto.*;
import com.imperial_net.inventioryApp.reports.service.ReportService;
import com.imperial_net.inventioryApp.sales.model.Sale;
import com.imperial_net.inventioryApp.sales.model.SaleDetail;
import com.imperial_net.inventioryApp.sales.repository.SaleDetailRepository;
import com.imperial_net.inventioryApp.sales.repository.SaleRepository;
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
 * Servicio para obtener datos y estadísticas del panel de control.
 */
@RequiredArgsConstructor
@Service
public class DashboarService {

    private final CookieService cookieService;
    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductService productService;
    private final ReportService reportService;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;

    /**
     * Obtiene la ganancia neta total del usuario.
     *
     * @param request la solicitud HTTP que contiene la cookie del usuario.
     * @return ganancia neta total.
     */
    public BigDecimal getTotalNetProfit(HttpServletRequest request) {
        User user = getUserFromCookie(request);

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

        return totalIncome.subtract(totalCost.add(totalExpenses));
    }

    /**
     * Obtiene la ganancia neta del mes actual.
     *
     * @param request la solicitud HTTP que contiene la cookie del usuario.
     * @return ganancia del mes actual.
     */
    public BigDecimal getCurrentMonthProfit(HttpServletRequest request) {
        User user = getUserFromCookie(request);
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate, user.getId());
        List<Expense> expenses = expenseRepository.findExpensesBetweenDates(startDate, endDate, user.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Sale sale : sales) {
            totalIncome = totalIncome.add(sale.getTotalSale());
            totalCost = totalCost.add(sale.getTotalCost());
        }

        for (Expense expense : expenses) {
            totalExpenses = totalExpenses.add(BigDecimal.valueOf(expense.getAmount()));
        }

        return totalIncome.subtract(totalCost.add(totalExpenses));
    }

    /**
     * Calcula la rentabilidad del negocio como porcentaje.
     *
     * @param request la solicitud HTTP que contiene la cookie del usuario.
     * @return rentabilidad en porcentaje.
     */
    public BigDecimal getTotalProfitability(HttpServletRequest request) {
        User user = getUserFromCookie(request);

        List<Sale> allSales = saleRepository.findByUser(user);
        List<Expense> allExpenses = expenseRepository.findAllByCreatedBy_Id(user.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Sale sale : allSales) {
            totalIncome = totalIncome.add(sale.getTotalSale());
            totalCost = totalCost.add(sale.getTotalCost());
        }

        for (Expense expense : allExpenses) {
            totalCost = totalCost.add(BigDecimal.valueOf(expense.getAmount()));
        }

        if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal netProfit = totalIncome.subtract(totalCost);
        return netProfit.divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Devuelve los 10 productos más vendidos por el usuario.
     *
     * @param request la solicitud HTTP que contiene la cookie del usuario.
     * @return lista de productos más vendidos.
     */
    public List<TopSellingProductResponse> getTop10MostSoldProducts(HttpServletRequest request) {
        User user = getUserFromCookie(request);

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.now();

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
     * Obtiene la lista de productos con bajo stock.
     *
     * @param request la solicitud HTTP que contiene la cookie del usuario.
     * @return lista de productos con stock bajo.
     */
    public List<StockLowDTO> getLowStockProducts(HttpServletRequest request) {
        return productService.findLowStockProducts(request);
    }

    /**
     * Devuelve la ganancia de los últimos 7 días.
     *
     * @param request la solicitud HTTP que contiene la cookie del usuario.
     * @return lista de ganancias diarias.
     */
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

    /**
     * Retorna la ganancia total del trimestre actual.
     *
     * @param request la solicitud HTTP.
     * @return ganancia del trimestre.
     */
    public DailyIncomeResponse getCurrentQuarterProfit(HttpServletRequest request) {
        return reportService.getCurrentQuarterIncome(request);
    }

    /**
     * Obtiene los clientes que más compraron este mes.
     *
     * @param request la solicitud HTTP.
     * @return lista de clientes top.
     */
    public List<TopCustomerResponse> getTopClientsThisMonth(HttpServletRequest request) {
        return reportService.getTopCustomersOfCurrentMonth(request);
    }

    /**
     * Devuelve el total de clientes registrados del usuario autenticado.
     *
     * @param request la solicitud HTTP.
     * @return número de clientes registrados.
     */
    public Long getTotalRegisteredClients(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return clientRepository.countClientsByUserId(userId);
    }

    /**
     * Devuelve el total de productos registrados por el usuario.
     *
     * @param request la solicitud HTTP.
     * @return número de productos registrados.
     */
    public Long getTotalRegisteredProducts(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return productRepository.countByRegistratedBy_Id(userId);
    }

    /**
     * Retorna el capital actualmente invertido en productos con stock.
     *
     * @param request la solicitud HTTP.
     * @return capital invertido.
     */
    public BigDecimal getTotalInvestedCapital(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        List<Purchase> purchases = purchaseRepository.findActivePurchasesWithStockByUser(userId);

        BigDecimal totalInvestment = BigDecimal.ZERO;

        for (Purchase purchase : purchases) {
            BigDecimal investment = purchase.getPurchasePrice().multiply(purchase.getRemainingStock());
            totalInvestment = totalInvestment.add(investment);
        }

        return totalInvestment;
    }

    /**
     * Devuelve la ganancia neta del día de hoy.
     *
     * @param request la solicitud HTTP.
     * @return ganancia de hoy.
     */
    public DailyIncomeResponse getTodayProfit(HttpServletRequest request) {
        return reportService.getTodayIncome(request);
    }

    /**
     * Compila y devuelve todos los datos del dashboard.
     *
     * @param request la solicitud HTTP.
     * @return objeto con todos los datos del panel.
     */
    public DashboardResponseDTO getDashboardData(HttpServletRequest request) {
        return new DashboardResponseDTO(
                getTotalNetProfit(request),
                getCurrentMonthProfit(request),
                getTotalProfitability(request),
                getTodayProfit(request).getGrossProfit(),
                getCurrentQuarterMonthlyProfits(request).stream()
                        .map(QuarterlyMonthProfitDTO::getProfit)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
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

    /**
     * Devuelve las ganancias mensuales correspondientes al trimestre actual.
     *
     * @param request la solicitud HTTP.
     * @return lista con las ganancias de cada mes del trimestre.
     */
    public List<QuarterlyMonthProfitDTO> getCurrentQuarterMonthlyProfits(HttpServletRequest request) {
        User user = getUserFromCookie(request);
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
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

    /**
     * Obtiene el usuario autenticado desde la cookie.
     *
     * @param request la solicitud HTTP.
     * @return el usuario autenticado.
     */
    private User getUserFromCookie(HttpServletRequest request) {
        return cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede procesar la solicitud."));
    }
}
