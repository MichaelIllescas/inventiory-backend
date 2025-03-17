package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.SaleRequestDTO;
import com.imperial_net.inventioryApp.dto.SaleResponseDTO;
import com.imperial_net.inventioryApp.services.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    public ResponseEntity<List<SaleResponseDTO>> getAllSales(HttpServletRequest request) {
        return ResponseEntity.ok(saleService.getAllSales(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        boolean deleted = saleService.deleteSale(id);

        if (deleted) {
            return ResponseEntity.ok().body("Venta eliminada correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la venta o no pudo eliminarse.");
        }
    }


}
