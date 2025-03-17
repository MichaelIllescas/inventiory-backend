package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.exceptions.ClientException;
import com.imperial_net.inventioryApp.models.*;
import com.imperial_net.inventioryApp.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final ClientService clientService;
    private final CookieService cookieService;
    private final PurchaseRepository purchaseRepository;
    /**
     * Crea una nueva venta a partir de un SaleRequestDTO.
     */
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO saleDTO, HttpServletRequest request) {
        // Obtener usuario desde la cookie
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ClientException("Usuario no autenticado. No se puede registrar la venta."));

        // Crear la venta
        Sale sale = new Sale();
        sale.setUser(user);
        sale.setPaymentMethod(PaymentMethod.valueOf(saleDTO.getPaymentMethod()));
        sale.setStatus(SaleStatus.CONFIRMED);
        sale.setDiscountApplied(saleDTO.getDiscountApplied() != null ? saleDTO.getDiscountApplied() : BigDecimal.ZERO);

        // Asociar cliente si se envió en la solicitud
        if (saleDTO.getClientId() != null) {
            sale.setCustomer(clientRepository.findById(saleDTO.getClientId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
        }

        // Procesar los detalles de venta con FIFO y reducción de stock
        List<SaleDetail> details = saleDTO.getProducts().stream().map(dto -> {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // Obtener el costo de compra real según FIFO
            BigDecimal costoUnitario = calcularCostoReal(dto.getProductId(), dto.getQuantity());

            // Reducir stock del producto
            if (product.getStock().compareTo(dto.getQuantity()) < 0) {
                throw new RuntimeException("Stock insuficiente para el producto ID: " + product.getId());
            }
            product.setStock(product.getStock().subtract(dto.getQuantity()));
            productRepository.save(product); // Guardar el nuevo stock

            // Crear detalle de venta
            SaleDetail detail = new SaleDetail();
            detail.setSale(sale);
            detail.setProduct(product);
            detail.setQuantity(dto.getQuantity());
            detail.setSalePrice(product.getSalePrice());
            detail.setCostPrice(costoUnitario);
            detail.setSubtotal(product.getSalePrice().multiply(dto.getQuantity()));

            return detail;
        }).collect(Collectors.toList());

        sale.setSaleDetails(details);
        calcularTotales(sale);

        // Guardar la venta en la base de datos
        Sale savedSale = saleRepository.save(sale);

        return convertToDTO(savedSale);
    }

    /**
     * Obtiene todas las ventas y las convierte a DTOs.
     */
    public List<SaleResponseDTO> getAllSales( HttpServletRequest request) {
       User user = cookieService.getUserFromCookie(request).orElseThrow( ()-> new RuntimeException("Usuario no autenticado"));


        return saleRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una venta por su ID y la convierte a DTO.
     */
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        return convertToDTO(sale);
    }

    /**
     * Calcula los totales de la venta.
     */
    private void calcularTotales(Sale sale) {
        BigDecimal totalSale = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (SaleDetail detail : sale.getSaleDetails()) {
            totalSale = totalSale.add(detail.getSubtotal());
            totalCost = totalCost.add(detail.getCostPrice().multiply(detail.getQuantity()));
        }

        BigDecimal discountPercentage = sale.getDiscountApplied() != null ? sale.getDiscountApplied() : BigDecimal.ZERO;

        // Convertir el porcentaje en un factor de multiplicación (Ej: 10% → 0.90)
        BigDecimal discountFactor = BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100)));

        // Aplicar el porcentaje de descuento
        BigDecimal totalSaleWithDiscount = totalSale.multiply(discountFactor).setScale(2, BigDecimal.ROUND_HALF_UP);

        // Calcular la ganancia bruta con el total con descuento
        BigDecimal grossProfit = totalSaleWithDiscount.subtract(totalCost);

        sale.setTotalSale(totalSaleWithDiscount); // Guardamos el total con el descuento aplicado
        sale.setTotalCost(totalCost);
        sale.setGrossProfit(grossProfit);
        sale.setNetProfit(grossProfit); // No es necesario restar descuento nuevamente porque ya está aplicado
    }

    /**
     * Convierte una entidad Sale en un SaleResponseDTO.
     */
    private SaleResponseDTO convertToDTO(Sale sale) {
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate());
        dto.setTotalSale(sale.getTotalSale());
        dto.setTotalCost(sale.getTotalCost());
        dto.setGrossProfit(sale.getGrossProfit());
        dto.setDiscountApplied(sale.getDiscountApplied());
        dto.setNetProfit(sale.getNetProfit());
        dto.setPaymentMethod(sale.getPaymentMethod().name());
        dto.setStatus(sale.getStatus().name());

        if (sale.getCustomer() != null) {
            dto.setClient(clientService.convertToDto(clientRepository.findById(sale.getCustomer().getId()).get()));
        }

        dto.setSaleDetails(sale.getSaleDetails().stream().map(detail -> {
            SaleDetailDTO detailDTO = new SaleDetailDTO();
            detailDTO.setId(detail.getId());
            detailDTO.setSaleId(sale.getId());

            // Verifica si el producto no es null antes de asignar sus valores
            if (detail.getProduct() != null) {
                detailDTO.setProductId(detail.getProduct().getId());
                detailDTO.setProductCode(detail.getProduct().getCode().toString());
                detailDTO.setProductName(detail.getProduct().getName());
                detailDTO.setProductDescription(detail.getProduct().getDescription());

                if (detail.getProduct().getCategory() != null) {
                    detailDTO.setProductCategory(detail.getProduct().getCategory());
                }

                if (detail.getProduct().getBrand() != null) {
                    detailDTO.setProductBrandName(detail.getProduct().getBrand().getName());
                }
            }

            detailDTO.setProductQuantity(detail.getQuantity());
            detailDTO.setProductSalePrice(detail.getSalePrice());
            detailDTO.setSubtotal(detail.getSubtotal());
            detailDTO.setCostPrice(detail.getCostPrice());

            return detailDTO;
        }).collect(Collectors.toList()));

        return dto;
    }

    private BigDecimal calcularCostoReal(Long productId, BigDecimal cantidadVendida) {
        List<Purchase> compras = purchaseRepository.findByProductIdAndStateOrderByPurchaseDateAsc(productId, true);

        if (compras.isEmpty()) {
            throw new RuntimeException("No hay historial de compras para el producto ID: " + productId);
        }

        BigDecimal cantidadRestante = cantidadVendida;
        BigDecimal costoTotal = BigDecimal.ZERO;

        for (Purchase compra : compras) {
            if (cantidadRestante.compareTo(BigDecimal.ZERO) == 0) break; // Si ya asignamos todo, salimos

            BigDecimal cantidadDisponible = compra.getRemainingStock();

            if (cantidadDisponible.compareTo(BigDecimal.ZERO) > 0) {
                // Se toma la menor cantidad entre lo disponible y lo requerido
                BigDecimal cantidadTomada = cantidadRestante.min(cantidadDisponible);
                BigDecimal costoParcial = cantidadTomada.multiply(compra.getPurchasePrice());

                // Restar del stock restante de esa compra
                compra.setRemainingStock(compra.getRemainingStock().subtract(cantidadTomada));

                // Si el stock de la compra llega a 0, pasamos a la siguiente
                if (compra.getRemainingStock().compareTo(BigDecimal.ZERO) == 0) {
                    compra.setState(false); // Opcional: Desactivar la compra si se agotó
                }

                purchaseRepository.save(compra);

                costoTotal = costoTotal.add(costoParcial);
                cantidadRestante = cantidadRestante.subtract(cantidadTomada);
            }
        }

        if (cantidadRestante.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("No hay suficiente stock disponible para el producto ID: " + productId);
        }

        return costoTotal.divide(cantidadVendida, 2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean deleteSale(Long id) {
        if (saleRepository.existsById(id)) { // Verifica si el ID existe antes de eliminar
            saleRepository.deleteById(id);
            return true; // Se eliminó correctamente
        }
        return false; // No se encontró el ID, no se eliminó nada
    }

}
