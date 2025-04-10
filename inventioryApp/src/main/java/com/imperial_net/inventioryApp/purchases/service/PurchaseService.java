package com.imperial_net.inventioryApp.purchases.service;

import com.imperial_net.inventioryApp.purchases.dto.PurchaseRequestDTO;
import com.imperial_net.inventioryApp.purchases.dto.PurchaseResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.exceptions.ProviderException;
import com.imperial_net.inventioryApp.products.models.Product;
import com.imperial_net.inventioryApp.products.service.ProductService;
import com.imperial_net.inventioryApp.providers.model.Provider;
import com.imperial_net.inventioryApp.providers.service.ProviderService;
import com.imperial_net.inventioryApp.purchases.model.Purchase;
import com.imperial_net.inventioryApp.purchases.repository.PurchaseRepository;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que maneja la lógica de negocio relacionada a compras de productos.
 */
@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;
    private final ProviderService providerService;
    private final CookieService cookieService;

    /**
     * Registra una nueva compra y actualiza el stock del producto.
     */
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("No se encontró una sesión válida. Inicie sesión para registrar compras."));

        Product product = productService.getProductById(purchaseRequestDTO.getProductId());
        Provider provider = providerService.getProviderById(purchaseRequestDTO.getProviderId());

        Purchase purchase = this.toEntity(purchaseRequestDTO);
        purchase.setProduct(product);
        purchase.setProvider(provider);
        purchase.setRegistredBy(user);

        BigDecimal nuevoStock = (product.getStock() == null ? BigDecimal.ZERO : product.getStock())
                .add(purchase.getQuantity());
        product.setStock(nuevoStock);
        productService.saveProduct(product);

        if (this.validateNumberOfRecords(user)) {
            purchaseRepository.save(purchase);
            return this.toResponseDTO(purchase);
        } else {
            throw new ProviderException("Ha alcanzado el límite de registros para el plan FREE. Si desea acceder a registros ilimitados, debe suscribirse al plan PRO");
        }
    }

    /**
     * Devuelve todas las compras registradas por el usuario autenticado.
     */
    public List<PurchaseResponseDTO> getAllPurchases(HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("No se encontró una sesión válida. Inicie sesión para ver sus compras."));

        return purchaseRepository.findAllByRegistredById(user.getId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza los datos de una compra existente.
     */
    public PurchaseResponseDTO updatePurchase(Long id, PurchaseRequestDTO purchaseRequestDTO, HttpServletRequest request) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ProductException("Compra no encontrada."));

        Provider providerUpdate = providerService.getProviderById(purchaseRequestDTO.getProviderId());

        purchase.setPurchasePrice(purchaseRequestDTO.getPurchasePrice());
        purchase.setQuantity(purchaseRequestDTO.getQuantity());
        purchase.setPurchaseDate(purchaseRequestDTO.getPurchaseDate());
        purchase.setNotes(purchaseRequestDTO.getNotes());
        purchase.setProvider(providerUpdate);

        Product product = productService.getProductByCode(purchaseRequestDTO.getProductCode(), request);
        product.setStock(purchaseRequestDTO.getQuantity()); // ⚠️ Esto sobrescribe el stock
        productService.saveProduct(product);

        purchaseRepository.save(purchase);
        return toResponseDTO(purchase);
    }

    /**
     * Cambia el estado de una compra (ACTIVO / ANULADO).
     */
    public PurchaseResponseDTO togglePurchaseStatus(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ProductException("Compra no encontrada."));

        purchase.setState(!purchase.getState());
        purchaseRepository.save(purchase);

        return toResponseDTO(purchase);
    }

    /**
     * Convierte una entidad Purchase en DTO.
     */
    public PurchaseResponseDTO toResponseDTO(Purchase purchase) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new PurchaseResponseDTO(
                purchase.getId(),
                purchase.getProduct().getName(),
                purchase.getProduct().getCode(),
                purchase.getPurchasePrice(),
                purchase.getQuantity(),
                purchase.getPurchaseDate().format(formatter),
                purchase.getProvider().getName(),
                purchase.getNotes(),
                purchase.getState() ? "ACTIVO" : "ANULADO"
        );
    }

    /**
     * Convierte un DTO en una entidad Purchase.
     */
    public Purchase toEntity(PurchaseRequestDTO dto) {
        Purchase purchase = new Purchase();
        purchase.setPurchasePrice(dto.getPurchasePrice());
        purchase.setQuantity(dto.getQuantity());
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setNotes(dto.getNotes());
        purchase.setState(dto.getState().equals("ACTIVO"));
        return purchase;
    }

    /**
     * Elimina una compra si no deja el stock negativo.
     */
    @Transactional
    public Boolean deletePurchase(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));

        try {
            Product product = purchase.getProduct();

            if (product.getStock().compareTo(purchase.getQuantity()) < 0) {
                throw new RuntimeException("No se puede eliminar la compra porque reduciría el stock a un valor negativo");
            }

            product.setStock(product.getStock().subtract(purchase.getQuantity()));
            productService.saveProduct(product);

            purchaseRepository.deleteById(id);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("No se puede eliminar la compra seleccionada: " + e.getMessage());
        }
    }

    /**
     * Valida si el usuario aún puede registrar compras en el plan actual.
     */
    public boolean validateNumberOfRecords(User user) {
        if (user.getSubscription() == Subscription.FREE) {
            return purchaseRepository.countByRegistredById(user.getId()) < 10;
        }
        return true;
    }
}
