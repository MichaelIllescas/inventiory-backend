package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.services.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;

    @GetMapping("/daily-income")
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(@RequestParam String date, HttpServletRequest request) {
        LocalDate selectedDate = LocalDate.parse(date);
        return ResponseEntity.ok(reportService.getDailyIncome(selectedDate, request));
    }

    @GetMapping("/monthly-income")
    public ResponseEntity<DailyIncomeResponse> getMonthlyIncome(@RequestParam String month, HttpServletRequest request) {
        YearMonth selectedMonth = YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getMonthlyIncome(selectedMonth, request));
    }

    @GetMapping("/annual-income")
    public ResponseEntity<DailyIncomeResponse> getAnnualIncome(@RequestParam Integer year, HttpServletRequest request) {
        return ResponseEntity.ok(reportService.getAnnualIncome(year, request));
    }

    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers(@RequestParam String month, HttpServletRequest request) {
        YearMonth selectedMonth = YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getTopCustomersForMonth(selectedMonth, request));
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts(@RequestParam String month, HttpServletRequest request) {
        YearMonth selectedMonth = YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getTopSellingProducts(selectedMonth, request));
    }

    @GetMapping("/profitability")
    public ResponseEntity<ProfitabilityDTO> getProfitability(@RequestParam Integer year, HttpServletRequest request) {
        return ResponseEntity.ok(reportService.getProfitabilityByYear(year, request));
    }
}
