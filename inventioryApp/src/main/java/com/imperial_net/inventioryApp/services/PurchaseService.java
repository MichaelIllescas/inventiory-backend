package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.PurchaseRequestDTO;
import com.imperial_net.inventioryApp.dto.PurchaseResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Provider;
import com.imperial_net.inventioryApp.models.Purchase;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.ProductRepository;
import com.imperial_net.inventioryApp.repositories.ProviderRepository;
import com.imperial_net.inventioryApp.repositories.PurchaseRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;
    private final ProviderService providerService;
    private final CookieService cookieService;

    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO, HttpServletRequest request) {
        // Obtener usuario autenticado
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("No se encontró una sesión válida. Inicie sesión para registrar compras."));

        // Obtener producto y proveedor, asegurando que existen
        Product product = productService.getProductById(purchaseRequestDTO.getProductId());
        Provider provider = providerService.getProviderById(purchaseRequestDTO.getProviderId());

        // Crear la entidad Purchase
        Purchase purchase = this.toEntity(purchaseRequestDTO);
        purchase.setProduct(product);
        purchase.setProvider(provider);
        purchase.setRegistredBy(user);

        // **Actualizar stock del producto**
        BigDecimal nuevoStock = (product.getStock() == null ? BigDecimal.ZERO : product.getStock()).add(purchase.getQuantity());
        product.setStock(nuevoStock);
        productService.saveProduct(product);

        // Guardar la compra
        purchaseRepository.save(purchase);

        return this.toResponseDTO(purchase);
    }

    public PurchaseResponseDTO toResponseDTO(Purchase purchase) {
        return new PurchaseResponseDTO(
                purchase.getId(),
                purchase.getProduct().getName(),
                purchase.getPurchasePrice(),
                purchase.getQuantity(),
                purchase.getPurchaseDate(),
                purchase.getProvider().getName(),
                purchase.getNotes()
        );
    }

    public Purchase toEntity(PurchaseRequestDTO dto) {
        Purchase purchase = new Purchase();
        purchase.setPurchasePrice(dto.getPurchasePrice());
        purchase.setQuantity(dto.getQuantity());
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setNotes(dto.getNotes());
        return purchase;
    }
}
