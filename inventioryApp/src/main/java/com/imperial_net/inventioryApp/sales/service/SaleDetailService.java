package com.imperial_net.inventioryApp.sales.service;

import com.imperial_net.inventioryApp.sales.dto.SaleDetailDTO;
import com.imperial_net.inventioryApp.products.models.Product;
import com.imperial_net.inventioryApp.sales.model.Sale;
import com.imperial_net.inventioryApp.sales.model.SaleDetail;
import com.imperial_net.inventioryApp.products.repository.ProductRepository;
import com.imperial_net.inventioryApp.sales.repository.SaleDetailRepository;
import com.imperial_net.inventioryApp.sales.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public SaleDetailService(SaleDetailRepository saleDetailRepository, SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleDetailRepository = saleDetailRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    /**
     * Crea un nuevo detalle de venta a partir de un SaleDetailDTO.
     */
    @Transactional
    public SaleDetailDTO createSaleDetail(SaleDetailDTO saleDetailDTO) {
        SaleDetail saleDetail = convertToEntity(saleDetailDTO);
        SaleDetail savedDetail = saleDetailRepository.save(saleDetail);
        return convertToDTO(savedDetail);
    }

    /**
     * Obtiene todos los detalles de venta y los convierte a DTOs.
     */
    public List<SaleDetailDTO> getAllSaleDetails() {
        return saleDetailRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un detalle de venta por su ID y lo convierte a DTO.
     */
    public Optional<SaleDetailDTO> getSaleDetailById(Long id) {
        return saleDetailRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Obtiene todos los detalles de una venta específica.
     */
    public List<SaleDetailDTO> getSaleDetailsBySale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Venta no Encontrada"));
        return saleDetailRepository.findBySale(sale).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las ventas en las que se vendió un producto específico.
     */
    public List<SaleDetailDTO> getSaleDetailsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return saleDetailRepository.findByProduct(product).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un detalle de venta existente.
     */
    @Transactional
    public SaleDetailDTO updateSaleDetail(Long id, SaleDetailDTO saleDetailDTO) {
        SaleDetail existingDetail = saleDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de Venta no encontrado"));

        existingDetail.setQuantity(saleDetailDTO.getProductQuantity());
        existingDetail.setSalePrice(saleDetailDTO.getProductSalePrice());
        existingDetail.setCostPrice(saleDetailDTO.getCostPrice());
        existingDetail.setSubtotal(saleDetailDTO.getProductSalePrice().multiply(saleDetailDTO.getProductQuantity()));

        SaleDetail updatedDetail = saleDetailRepository.save(existingDetail);
        return convertToDTO(updatedDetail);
    }

    /**
     * Elimina un detalle de venta por su ID.
     */
    @Transactional
    public void deleteSaleDetail(Long id) {
        saleDetailRepository.deleteById(id);
    }

    /**
     * Convierte un SaleDetailDTO en una entidad SaleDetail.
     */
    private SaleDetail convertToEntity(SaleDetailDTO dto) {
        SaleDetail detail = new SaleDetail();
        detail.setSale(saleRepository.findById(dto.getSaleId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada")));
        detail.setProduct(productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado")));
        detail.setQuantity(dto.getProductQuantity());
        detail.setSalePrice(dto.getProductSalePrice());
        detail.setCostPrice(dto.getCostPrice());
        detail.setSubtotal(dto.getProductSalePrice().multiply(dto.getProductQuantity())); // Calcula subtotal
        return detail;
    }

    /**
     * Convierte una entidad SaleDetail en un SaleDetailDTO.
     */
    private SaleDetailDTO convertToDTO(SaleDetail detail) {
        SaleDetailDTO dto = new SaleDetailDTO();
        dto.setId(detail.getId());
        dto.setSaleId(detail.getSale().getId());
        dto.setProductId(detail.getProduct().getId());
        dto.setProductQuantity(detail.getQuantity());
        dto.setProductSalePrice(detail.getSalePrice());
        dto.setSubtotal(detail.getSubtotal());
        dto.setCostPrice(detail.getCostPrice());
        dto.setProductName(detail.getProduct().getName());
        dto.setProductCode(detail.getProduct().getCode().toString());
        dto.setProductCategory(detail.getProduct().getBrand().getName());
        dto.setProductId(detail.getProduct().getId());
        return dto;
    }
}
