package com.imperial_net.inventioryApp.controllers;


import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO, HttpServletRequest request) {
        try {
            ProductResponseDTO savedProduct = productService.save(productRequestDTO, request);
            if (savedProduct == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No se pudo registrar el producto. Verifique los datos ingresados.");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al registrar el producto: " + e.getMessage());
        }
    }

    @GetMapping("/getProducts")
    public ResponseEntity<?> getProducts(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProductsForUser(request));
    }

    @GetMapping("/getProductsActive")
    public ResponseEntity<?> getProductsActive(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProductsActiveForUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateProduct(
            @PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO
    ){
        productService.updateProduct(id,productRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("producto actualizado correctamente");
    }




    @PatchMapping("/{id}/stock")
    public ResponseEntity<?>updateStock(
            @PathVariable Long id, @RequestBody ProductUpdateStockDTO productUpdateStockDTO
    ){
        productService.updateStock(id,productUpdateStockDTO);
        return ResponseEntity.status(HttpStatus.OK).body("producto actualizado correctamente");
    }

    @PutMapping("/modify-prices")
    public ResponseEntity<?> modifyPrices(@RequestBody UpdatePriceDTO updatePriceDTO) {
        try {
            productService.modifyProductPrices(updatePriceDTO);
            return ResponseEntity.ok("Precios modificados correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar los precios");
        }
    }

    @PatchMapping("/toggleStatus/{id}")
    public ResponseEntity<?>updateState(
            @PathVariable Long id){
        try {
            productService.updateState(id);
            return ResponseEntity.ok("Estado actualziado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar los precios");
        }
    }
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockLowDTO>> getLowStockProducts(HttpServletRequest request) {
        List<StockLowDTO> lowStockProducts = productService.findLowStockProducts(request);
        return ResponseEntity.ok(lowStockProducts);
    }

}
