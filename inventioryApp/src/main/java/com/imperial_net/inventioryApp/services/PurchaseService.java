package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.PurchaseRequestDTO;
import com.imperial_net.inventioryApp.dto.PurchaseResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Provider;
import com.imperial_net.inventioryApp.models.Purchase;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.PurchaseRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;
    private final ProviderService providerService;
    private final CookieService cookieService;

    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO purchaseRequestDTO, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("No se encontró una sesión válida. Inicie sesión para registrar compras."));

        Product product = productService.getProductById(purchaseRequestDTO.getProductId());
        Provider provider = providerService.getProviderById(purchaseRequestDTO.getProviderId());

        Purchase purchase = this.toEntity(purchaseRequestDTO);
        purchase.setProduct(product);
        purchase.setProvider(provider);
        purchase.setRegistredBy(user);

        BigDecimal nuevoStock = (product.getStock() == null ? BigDecimal.ZERO : product.getStock()).add(purchase.getQuantity());
        product.setStock(nuevoStock);
        productService.saveProduct(product);

        purchaseRepository.save(purchase);
        return this.toResponseDTO(purchase);
    }

    public List<PurchaseResponseDTO> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PurchaseResponseDTO updatePurchase(Long id, PurchaseRequestDTO purchaseRequestDTO) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ProductException("Compra no encontrada."));

        Provider providerUpdate= providerService.getProviderById(purchaseRequestDTO.getProviderId());
        purchase.setPurchasePrice(purchaseRequestDTO.getPurchasePrice());
        purchase.setQuantity(purchaseRequestDTO.getQuantity());
        purchase.setPurchaseDate(purchaseRequestDTO.getPurchaseDate());
        purchase.setNotes(purchaseRequestDTO.getNotes());
        purchase.setProvider(providerUpdate);

        purchaseRepository.save(purchase);
        return toResponseDTO(purchase);
    }

    public PurchaseResponseDTO togglePurchaseStatus(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ProductException("Compra no encontrada."));

        if (purchase.getState()){
            purchase.setState(false);
        }else {
            purchase.setState(true);
        }

        purchaseRepository.save(purchase);

        return toResponseDTO(purchase);
    }

    public PurchaseResponseDTO toResponseDTO(Purchase purchase)     {
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
                purchase.getState()?"ACTIVO":"ANULADO"

        );
    }

    public Purchase toEntity(PurchaseRequestDTO dto) {
        Purchase purchase = new Purchase();
        purchase.setPurchasePrice(dto.getPurchasePrice());
        purchase.setQuantity(dto.getQuantity());
        purchase.setPurchaseDate(dto.getPurchaseDate());
        purchase.setNotes(dto.getNotes());
        purchase.setState((dto.getState().equals("ACTIVO")? true:false) );
        return purchase;
    }
}