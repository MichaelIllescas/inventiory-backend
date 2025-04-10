package com.imperial_net.inventioryApp.sales.service;

import com.imperial_net.inventioryApp.clients.repository.ClientRepository;
import com.imperial_net.inventioryApp.clients.service.ClientService;
import com.imperial_net.inventioryApp.exceptions.ClientException;
import com.imperial_net.inventioryApp.products.models.Product;
import com.imperial_net.inventioryApp.products.repository.ProductRepository;
import com.imperial_net.inventioryApp.purchases.model.Purchase;
import com.imperial_net.inventioryApp.purchases.repository.PurchaseRepository;
import com.imperial_net.inventioryApp.sales.dto.SaleDetailDTO;
import com.imperial_net.inventioryApp.sales.dto.SaleRequestDTO;
import com.imperial_net.inventioryApp.sales.dto.SaleResponseDTO;
import com.imperial_net.inventioryApp.sales.model.PaymentMethod;
import com.imperial_net.inventioryApp.sales.model.Sale;
import com.imperial_net.inventioryApp.sales.model.SaleDetail;
import com.imperial_net.inventioryApp.sales.model.SaleStatus;
import com.imperial_net.inventioryApp.sales.repository.SaleRepository;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
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
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ClientException("Usuario no autenticado. No se puede registrar la venta."));

        Sale sale = new Sale();
        sale.setUser(user);
        sale.setPaymentMethod(PaymentMethod.valueOf(saleDTO.getPaymentMethod()));
        sale.setStatus(SaleStatus.CONFIRMED);
        sale.setDiscountApplied(saleDTO.getDiscountApplied() != null ? saleDTO.getDiscountApplied() : BigDecimal.ZERO);
        sale.setExtra_charge_percentage(saleDTO.getExtra_charge_percentage() != null ? saleDTO.getExtra_charge_percentage() : BigDecimal.ZERO);
        if (saleDTO.getClientId() != null) {
            sale.setCustomer(clientRepository.findById(saleDTO.getClientId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
        }

        List<SaleDetail> details = saleDTO.getProducts().stream().map(dto -> {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            BigDecimal totalStockDisponible = product.getStock();
            if (totalStockDisponible.compareTo(dto.getQuantity()) < 0) {
                throw new RuntimeException("Stock insuficiente para el producto Código: " + product.getCode() + " - " + product.getName() +". Cantidad disponible: " + product.getStock().setScale(2, RoundingMode.HALF_UP)
                );
            }

            BigDecimal costoUnitario = calcularCostoReal(dto.getProductId(), dto.getQuantity());

            product.setStock(product.getStock().subtract(dto.getQuantity()));
            productRepository.save(product);

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

        if (this.validateNumberOfRecords(user)){
            Sale savedSale = saleRepository.save(sale);
            return convertToDTO(savedSale);
        }else {
            throw new RuntimeException(     "Ha alcanzado el límite de registros para el plan FREE. Si desea acceder a registros ilimiatados, debe suscribirse al plan PRO");
        }


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

        // Obtener descuento y aumento
        BigDecimal discountPercentage = sale.getDiscountApplied() != null ? sale.getDiscountApplied() : BigDecimal.ZERO;
        BigDecimal extraChargePercentage = sale.getExtra_charge_percentage() != null ? sale.getExtra_charge_percentage() : BigDecimal.ZERO;

        // Convertir los porcentajes en factores
        BigDecimal discountFactor = BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        BigDecimal extraChargeFactor = BigDecimal.ONE.add(extraChargePercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

        // Aplicar descuento y aumento
        BigDecimal totalSaleWithDiscount = totalSale.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalSaleWithIncrease = totalSaleWithDiscount.multiply(extraChargeFactor).setScale(2, RoundingMode.HALF_UP);

        // Calcular la ganancia bruta con el total ajustado
        BigDecimal grossProfit = totalSaleWithIncrease.subtract(totalCost);

        // Guardar los valores en la venta
        sale.setTotalSale(totalSaleWithIncrease); // Ahora incluye descuentos y aumentos
        sale.setTotalCost(totalCost);
        sale.setGrossProfit(grossProfit);
        sale.setNetProfit(grossProfit);
    }

    /**
     * Convierte una entidad Sale en un SaleResponseDTO.
     */
    private SaleResponseDTO convertToDTO(Sale sale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        SaleResponseDTO dto = new SaleResponseDTO();
        dto.setId(sale.getId());
        dto.setSaleDate(sale.getSaleDate().format(formatter));
        dto.setTotalSale(sale.getTotalSale());
        dto.setTotalCost(sale.getTotalCost());
        dto.setGrossProfit(sale.getGrossProfit());
        dto.setDiscountApplied(sale.getDiscountApplied());
        dto.setNetProfit(sale.getNetProfit());
        dto.setPaymentMethod(sale.getPaymentMethod().getDescripcion());
        dto.setStatus(sale.getStatus().getDescripcion());
        dto.setExtra_charge_percentage(sale.getExtra_charge_percentage());

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

        compras = compras.stream()
                .filter(p -> p.getRemainingStock().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        if (compras.isEmpty()) {
            throw new RuntimeException("No hay compras disponibles con stock para el producto Código: " + productRepository.findById(productId).get().getCode() );
        }

        BigDecimal cantidadRestante = cantidadVendida;
        BigDecimal costoTotal = BigDecimal.ZERO;

        for (Purchase compra : compras) {
            if (cantidadRestante.compareTo(BigDecimal.ZERO) == 0) break;

            BigDecimal cantidadDisponible = compra.getRemainingStock();
            BigDecimal cantidadTomada = cantidadRestante.min(cantidadDisponible);
            BigDecimal costoParcial = cantidadTomada.multiply(compra.getPurchasePrice());

            compra.setRemainingStock(compra.getRemainingStock().subtract(cantidadTomada));

            if (compra.getRemainingStock().compareTo(BigDecimal.ZERO) == 0) {
                compra.setState(false);
            }

            purchaseRepository.save(compra);

            costoTotal = costoTotal.add(costoParcial);
            cantidadRestante = cantidadRestante.subtract(cantidadTomada);
        }

        if (cantidadRestante.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Stock insuficiente para completar la venta del producto Código: " + productRepository.findById(productId).get().getCode() );
        }

        return costoTotal.divide(cantidadVendida, 2, BigDecimal.ROUND_HALF_UP);
    }

    @Transactional
    public boolean deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (sale.getStatus().equals(SaleStatus.CANCELED)) {
            throw new RuntimeException("No se puede eliminar una venta que ya ha sido anulada.");
        }

        restoreStockFromSale(sale);         // Restaurar stock del producto
        restorePurchasesFromSale(sale);     // Restaurar compras usadas en esta venta

        saleRepository.delete(sale);
        return true;
    }


    @Transactional
    public SaleResponseDTO changeState(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (sale.getStatus().equals(SaleStatus.CONFIRMED)) {
            sale.setStatus(SaleStatus.CANCELED);
            restoreStockFromSale(sale); // 🟢 Restaurar stock al anular
        } else if (sale.getStatus().equals(SaleStatus.CANCELED)) {
            sale.setStatus(SaleStatus.CONFIRMED);
            deductStockFromSale(sale); // 🔴 Restar stock al confirmar de nuevo
        }

        return this.convertToDTO(saleRepository.save(sale));
    }

    private void restoreStockFromSale(Sale sale) {
        for (SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock().add(detail.getQuantity()));
            productRepository.save(product);
        }
    }

    private void deductStockFromSale(Sale sale) {
        for (SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            if (product.getStock().compareTo(detail.getQuantity()) < 0) {
                throw new RuntimeException("Stock insuficiente para reactivar la venta del producto ID: " + product.getId());
            }
            product.setStock(product.getStock().subtract(detail.getQuantity()));
            productRepository.save(product);
        }
    }

    public List<SaleResponseDTO> getSalesToClient(Long id) {
         return  saleRepository.findAllByCustomerId (id).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private void restorePurchasesFromSale(Sale sale) {
        for (SaleDetail detail : sale.getSaleDetails()) {
            BigDecimal cantidadRestante = detail.getQuantity();
            BigDecimal costoUnitario = detail.getCostPrice();

            // Buscar compras que coincidan con el precio de costo y el producto
            List<Purchase> compras = purchaseRepository
                    .findByProductIdOrderByPurchaseDateAsc(detail.getProduct().getId());

            for (Purchase compra : compras) {
                if (compra.getPurchasePrice().compareTo(costoUnitario) == 0) {
                    // Revertir la cantidad usada
                    BigDecimal cantidadARestaurar = cantidadRestante.min(compra.getQuantity().subtract(compra.getRemainingStock()));
                    compra.setRemainingStock(compra.getRemainingStock().add(cantidadARestaurar));

                    // Restaurar el estado si es necesario
                    if (compra.getRemainingStock().compareTo(BigDecimal.ZERO) > 0) {
                        compra.setState(true);
                    }

                    purchaseRepository.save(compra);
                    cantidadRestante = cantidadRestante.subtract(cantidadARestaurar);

                    if (cantidadRestante.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }
                }
            }

            if (cantidadRestante.compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("No se pudo restaurar completamente el stock de compras para el producto " + detail.getProduct().getName());
            }
        }
    }

    //DEVUELVE TRUE SI ESTA TODO OK PARA CONTINUAR
    public boolean validateNumberOfRecords(User user) {
        if (user.getSubscription() == Subscription.FREE) {
            return saleRepository.countByUser_Id(user.getId()) < 10;
        }
        return true;
    }


}
