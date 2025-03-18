package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.DailyIncomeResponse;
import com.imperial_net.inventioryApp.dto.ProfitabilityDTO;
import com.imperial_net.inventioryApp.dto.TopCustomerResponse;
import com.imperial_net.inventioryApp.dto.TopSellingProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imperial_net.inventioryApp.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin("*")// Permite llamadas desde el frontend
@RequiredArgsConstructor
public class ReportsController {

    private final ReportService reportService;



    @GetMapping("/daily-income")
    public ResponseEntity<DailyIncomeResponse> getDailyIncome(@RequestParam String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        DailyIncomeResponse response = reportService.getDailyIncome(selectedDate);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/monthly-income")
    public ResponseEntity<DailyIncomeResponse> getMonthlyIncome(@RequestParam String month) {
        YearMonth selectedMonth = YearMonth.parse(month);
        DailyIncomeResponse response = reportService.getMonthlyIncome(selectedMonth);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/annual-income")
    public ResponseEntity<DailyIncomeResponse> getAnnualIncome(@RequestParam String year) {
        int selectedYear = Integer.parseInt(year);
        DailyIncomeResponse response = reportService.getAnnualIncome(selectedYear);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers(@RequestParam String month) {
        YearMonth selectedMonth = YearMonth.parse(month);
        List<TopCustomerResponse> response = reportService.getTopCustomersForMonth(selectedMonth);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/top-selling-products")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts(@RequestParam String month) {
        YearMonth selectedMonth = YearMonth.parse(month);
        List<TopSellingProductResponse> response = reportService.getTopSellingProducts(selectedMonth);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/profitability")
    public ResponseEntity<ProfitabilityDTO> getProfitability(@RequestParam Integer year) {
        int selectedYear = year;
        ProfitabilityDTO response = reportService.getProfitabilityByYear(selectedYear);
        return ResponseEntity.ok(response);
    }



}

