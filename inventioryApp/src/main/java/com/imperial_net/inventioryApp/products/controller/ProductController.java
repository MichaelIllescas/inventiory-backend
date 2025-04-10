package com.imperial_net.inventioryApp.products.controller;

import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.products.dto.*;
import com.imperial_net.inventioryApp.products.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST encargado de gestionar productos.
 * Permite registrar, consultar, modificar, actualizar stock y estado, y manejar precios.
 */
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Registra un nuevo producto para el usuario autenticado.
     *
     * @param productRequestDTO datos del nuevo producto.
     * @param request           solicitud HTTP con la sesión del usuario.
     * @return producto creado o mensaje de error.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerNewProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO,
            HttpServletRequest request) {

        ProductResponseDTO savedProduct = productService.save(productRequestDTO, request);

        if (savedProduct == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se pudo registrar el producto. Verifique los datos ingresados."));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * Devuelve todos los productos del usuario autenticado (activos e inactivos).
     *
     * @param request solicitud HTTP.
     * @return lista de productos.
     */
    @GetMapping("/getProducts")
    public ResponseEntity<?> getProducts(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProductsForUser(request));
    }

    /**
     * Devuelve todos los productos activos del usuario autenticado.
     *
     * @param request solicitud HTTP.
     * @return lista de productos activos.
     */
    @GetMapping("/getProductsActive")
    public ResponseEntity<?> getProductsActive(HttpServletRequest request) {
        return ResponseEntity.ok(productService.getAllProductsActiveForUser(request));
    }

    /**
     * Actualiza la información de un producto existente.
     *
     * @param id                 ID del producto.
     * @param productRequestDTO  datos actualizados del producto.
     * @return mensaje de éxito.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO productRequestDTO) {

        productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Producto actualizado correctamente");
    }

    /**
     * Actualiza el stock de un producto.
     *
     * @param id                    ID del producto.
     * @param productUpdateStockDTO datos de stock.
     * @return mensaje de éxito.
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long id,
            @RequestBody ProductUpdateStockDTO productUpdateStockDTO) {

        productService.updateStock(id, productUpdateStockDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Stock actualizado correctamente");
    }

    /**
     * Modifica los precios de múltiples productos según los criterios especificados.
     *
     * @param updatePriceDTO DTO con los criterios de actualización.
     * @return mensaje de éxito o error.
     */
    @PutMapping("/modify-prices")
    public ResponseEntity<?> modifyPrices(@RequestBody UpdatePriceDTO updatePriceDTO) {
        try {
            productService.modifyProductPrices(updatePriceDTO);
            return ResponseEntity.ok("Precios modificados correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar los precios");
        }
    }

    /**
     * Alterna (activa/desactiva) el estado de un producto.
     *
     * @param id ID del producto.
     * @return mensaje de éxito o error.
     */
    @PatchMapping("/toggleStatus/{id}")
    public ResponseEntity<?> updateState(@PathVariable Long id) {
        try {
            productService.updateState(id);
            return ResponseEntity.ok("Estado actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el estado");
        }
    }

    /**
     * Devuelve los productos con bajo nivel de stock para el usuario autenticado.
     *
     * @param request solicitud HTTP.
     * @return lista de productos con stock bajo.
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<StockLowDTO>> getLowStockProducts(HttpServletRequest request) {
        List<StockLowDTO> lowStockProducts = productService.findLowStockProducts(request);
        return ResponseEntity.ok(lowStockProducts);
    }


}
