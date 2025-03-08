package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.PurchaseRequestDTO;
import com.imperial_net.inventioryApp.dto.PurchaseResponseDTO;
import com.imperial_net.inventioryApp.services.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
