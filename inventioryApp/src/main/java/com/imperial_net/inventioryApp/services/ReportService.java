package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.DailyIncomeResponse;
import com.imperial_net.inventioryApp.dto.TopCustomerResponse;
import com.imperial_net.inventioryApp.dto.TopSellingProductResponse;
import com.imperial_net.inventioryApp.models.Sale;
import com.imperial_net.inventioryApp.models.SaleDetail;
import com.imperial_net.inventioryApp.models.Purchase;
import com.imperial_net.inventioryApp.repositories.SaleDetailRepository;
import com.imperial_net.inventioryApp.repositories.SaleRepository;
import com.imperial_net.inventioryApp.repositories.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final SaleRepository saleRepository;
    private final PurchaseRepository purchaseRepository;
    private final SaleDetailRepository saleDetailRepository;



    public DailyIncomeResponse getDailyIncome(LocalDate date) {
        List<Sale> sales = saleRepository.findSalesByDate(date);

        BigDecimal grossIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        // Mapear compras por producto para hacer FIFO
        Map<Long, List<Purchase>> purchasesByProduct = purchaseRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getProduct().getId()));

        for (Sale sale : sales) {
            grossIncome = grossIncome.add(sale.getTotalSale());
            totalDiscount = totalDiscount.add(sale.getDiscountApplied());

            for (SaleDetail detail : sale.getSaleDetails()) {
                BigDecimal quantityToAssign = detail.getQuantity();
                List<Purchase> purchases = purchasesByProduct.get(detail.getProduct().getId());


                // Ordenar compras por fecha (FIFO)
                purchases.sort((p1, p2) -> p1.getPurchaseDate().compareTo(p2.getPurchaseDate()));

                for (Purchase purchase : purchases) {
                    if (quantityToAssign.compareTo(BigDecimal.ZERO) <= 0) break;

                    BigDecimal availableStock = purchase.getRemainingStock();
                    if (availableStock.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal usedStock = availableStock.min(quantityToAssign);
                        BigDecimal costForThisSale = usedStock.multiply(purchase.getPurchasePrice());

                        totalCost = totalCost.add(costForThisSale);
                        purchase.setRemainingStock(availableStock.subtract(usedStock));
                        quantityToAssign = quantityToAssign.subtract(usedStock);

                    }
                }
            }
        }

        // Calcular ganancias
        BigDecimal grossProfit = grossIncome.subtract(totalCost);
        BigDecimal netProfit = grossProfit.subtract(totalDiscount);


        return new DailyIncomeResponse(grossIncome, totalCost, totalDiscount, grossProfit, netProfit);
    }

    public DailyIncomeResponse getMonthlyIncome(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);

        BigDecimal grossIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Sale sale : sales) {
            grossIncome = grossIncome.add(sale.getTotalSale());
            totalDiscount = totalDiscount.add(sale.getDiscountApplied());
            totalCost = totalCost.add(sale.getTotalCost());
        }

        BigDecimal grossProfit = grossIncome.subtract(totalCost);
        BigDecimal netProfit = grossProfit.subtract(totalDiscount);

        return new DailyIncomeResponse(grossIncome, totalCost, totalDiscount, grossProfit, netProfit);
    }

    public DailyIncomeResponse getAnnualIncome(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Sale> sales = saleRepository.findSalesBetweenDates(startDate, endDate);

        BigDecimal grossIncome = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Sale sale : sales) {
            grossIncome = grossIncome.add(sale.getTotalSale());
            totalDiscount = totalDiscount.add(sale.getDiscountApplied());
            totalCost = totalCost.add(sale.getTotalCost());
        }

        BigDecimal grossProfit = grossIncome.subtract(totalCost);
        BigDecimal netProfit = grossProfit.subtract(totalDiscount);

        return new DailyIncomeResponse(grossIncome, totalCost, totalDiscount, grossProfit, netProfit);
    }

    public List<TopCustomerResponse> getTopCustomersForMonth(YearMonth month) {
        LocalDate startDate = month.atDay(1);      // Primer día del mes
        LocalDate endDate = month.atEndOfMonth();  // Último día del mes

        List<Object[]> results = saleRepository.findTopCustomersForMonth(startDate, endDate);

        return results.stream()
                .map(row -> new TopCustomerResponse(
                        (String) row[0] +' ' + (String) row[1] , // Nombre del cliente
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


}
