package com.imperial_net.inventioryApp.purchases.controller;

import com.imperial_net.inventioryApp.purchases.dto.PurchaseRequestDTO;
import com.imperial_net.inventioryApp.purchases.dto.PurchaseResponseDTO;
import com.imperial_net.inventioryApp.purchases.service.PurchaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que gestiona las operaciones de compras.
 * Permite registrar, actualizar, listar, anular y eliminar compras.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * Registra una nueva compra.
     *
     * @param purchaseRequestDTO DTO con los datos de la compra.
     * @param request             solicitud HTTP con datos del usuario autenticado.
     * @return compra registrada.
     */
    @PostMapping("/register")
    public ResponseEntity<PurchaseResponseDTO> createPurchase(
            @RequestBody PurchaseRequestDTO purchaseRequestDTO,
            HttpServletRequest request) {

        PurchaseResponseDTO responseDTO = purchaseService.createPurchase(purchaseRequestDTO, request);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Obtiene todas las compras realizadas por el usuario autenticado.
     *
     * @param request solicitud HTTP.
     * @return lista de compras.
     */
    @GetMapping("/getPurchases")
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases(HttpServletRequest request) {
        List<PurchaseResponseDTO> purchases = purchaseService.getAllPurchases(request);
        return ResponseEntity.ok(purchases);
    }

    /**
     * Actualiza una compra existente.
     *
     * @param id                 ID de la compra a actualizar.
     * @param purchaseRequestDTO nuevos datos.
     * @param request            solicitud HTTP.
     * @return compra actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PurchaseResponseDTO> updatePurchase(
            @PathVariable Long id,
            @RequestBody PurchaseRequestDTO purchaseRequestDTO,
            HttpServletRequest request) {

        PurchaseResponseDTO updatedPurchase = purchaseService.updatePurchase(id, purchaseRequestDTO, request);
        return ResponseEntity.ok(updatedPurchase);
    }

    /**
     * Cambia el estado (activo/inactivo) de una compra.
     *
     * @param id ID de la compra.
     * @return compra con estado actualizado.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PurchaseResponseDTO> togglePurchaseStatus(@PathVariable Long id) {
        PurchaseResponseDTO updatedPurchase = purchaseService.togglePurchaseStatus(id);
        return ResponseEntity.ok(updatedPurchase);
    }

    /**
     * Elimina lógicamente una compra (anulación).
     *
     * @param id ID de la compra a eliminar.
     * @return mensaje de éxito.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePurchase(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.deletePurchase(id));
    }
}
