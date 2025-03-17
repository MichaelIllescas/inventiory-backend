package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.SaleRequestDTO;
import com.imperial_net.inventioryApp.dto.SaleResponseDTO;
import com.imperial_net.inventioryApp.services.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    @PostMapping("/register")
    public ResponseEntity<SaleResponseDTO> createSale(
            @Valid @RequestBody SaleRequestDTO saleDTO,
            HttpServletRequest request) {
        SaleResponseDTO createdSale = saleService.createSale(saleDTO, request);
        return ResponseEntity.ok(createdSale);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }
}
