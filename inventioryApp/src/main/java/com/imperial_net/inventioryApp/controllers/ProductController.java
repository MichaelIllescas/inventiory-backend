package com.imperial_net.inventioryApp.controllers;


import com.imperial_net.inventioryApp.dto.ProductRequestDTO;
import com.imperial_net.inventioryApp.dto.ProductResponseDTO;
import com.imperial_net.inventioryApp.dto.ProductUpdateStockDTO;
import com.imperial_net.inventioryApp.dto.UpdatePriceDTO;
import com.imperial_net.inventioryApp.services.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO, HttpServletRequest request
            )
    {
        ProductResponseDTO savedProduct = productService.save(productRequestDTO, request);
        return ResponseEntity.ok(savedProduct);

    }

        @GetMapping("/getProducts")
    public ResponseEntity<?> getProducts(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProductsForUser(request));
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


}
