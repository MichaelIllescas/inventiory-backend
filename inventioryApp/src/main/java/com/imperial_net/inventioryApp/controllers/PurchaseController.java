package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.PurchaseRequestDTO;
import com.imperial_net.inventioryApp.dto.PurchaseResponseDTO;
import com.imperial_net.inventioryApp.services.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/register")
    public ResponseEntity<PurchaseResponseDTO> createPurchase(@RequestBody PurchaseRequestDTO purchaseRequestDTO, HttpServletRequest request) {
        PurchaseResponseDTO responseDTO = purchaseService.createPurchase(purchaseRequestDTO, request);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping ("/getPurchases")
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases() {
        List<PurchaseResponseDTO> purchases = purchaseService.getAllPurchases();
        return ResponseEntity.ok(purchases);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> updatePurchase(@PathVariable Long id, @RequestBody PurchaseRequestDTO purchaseRequestDTO) {
        PurchaseResponseDTO updatedPurchase = purchaseService.updatePurchase(id, purchaseRequestDTO);
        return ResponseEntity.ok(updatedPurchase);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseResponseDTO> togglePurchaseStatus(@PathVariable Long id) {
        PurchaseResponseDTO updatedPurchase = purchaseService.togglePurchaseStatus(id);
        return ResponseEntity.ok(updatedPurchase);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePurchase(@PathVariable Long id){
        return ResponseEntity.ok(purchaseService.deletePurchase(id));
    }
}