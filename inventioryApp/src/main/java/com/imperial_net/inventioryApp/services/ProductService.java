package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.exceptions.ProviderException;
import com.imperial_net.inventioryApp.models.Brand;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Provider;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.BrandRepository;
import com.imperial_net.inventioryApp.repositories.ProductRepository;
import com.imperial_net.inventioryApp.repositories.ProviderRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CookieService cookieService;
    private final BrandRepository brandRepository;
    private final ProviderRepository providerRepository;

    public ProductResponseDTO convertToDto(Product product) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPurchasePrice(product.getPurchasePrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setStock(product.getStock());
        dto.setMinStock(product.getMinStock());
        dto.setCategory(product.getCategory());
        dto.setProviderName((product.getProvider() != null) ? product.getProvider().getName() : null);
        dto.setRegistratedByName((product.getRegistratedBy() != null) ? product.getRegistratedBy().getUsername() : null);
        dto.setBrandName((product.getBrand() != null) ? product.getBrand().getName() : null);
        dto.setRegistrationDate((product.getRegistrationDate() != null) ? product.getRegistrationDate().format(formatter) : null);
        dto.setUpdatedDate((product.getUpdatedDate() != null) ? product.getUpdatedDate().format(formatter) : null);
        dto.setLastPriceUpdate((product.getLastPriceUpdate() != null) ? product.getLastPriceUpdate().format(formatter) : null);

        return dto;
    }


    @Transactional
    public ProductResponseDTO save(@Valid ProductRequestDTO productRequestDTO, HttpServletRequest request) {

        // Obtener usuario autenticado desde la cookie
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProviderException("Usuario no autenticado. No se puede registrar el producto."));

        // Verificar que el proveedor existe
        Provider provider = providerRepository.findById(productRequestDTO.getProviderId())
                .orElseThrow(() -> new ProviderException("El proveedor con ID " + productRequestDTO.getProviderId() + " no existe."));

        // Buscar o crear la marca
        Brand brand = brandRepository.findByName(productRequestDTO.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(productRequestDTO.getBrandName());
                    return brandRepository.save(newBrand);
                });

        // Verificar si ya existe un producto con el mismo código
        if (productRepository.existsByCode(productRequestDTO.getCode())) {
            throw new ProviderException("Ya existe un producto con el código '" + productRequestDTO.getCode() + "'.");
        }

        // Validar que los precios sean mayores a 0
        if (productRequestDTO.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0 ||
                productRequestDTO.getSalePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProviderException("Los precios de compra y venta deben ser mayores a 0.");
        }

        // Crear y guardar el producto
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setCode(productRequestDTO.getCode());
        product.setBrand(brand);
        product.setDescription(productRequestDTO.getDescription());
        product.setPurchasePrice(productRequestDTO.getPurchasePrice());
        product.setSalePrice(productRequestDTO.getSalePrice());
        product.setStock(productRequestDTO.getStock());
        product.setMinStock(productRequestDTO.getMinStock());
        product.setCategory(productRequestDTO.getCategory());
        product.setProvider(provider);
        product.setRegistratedBy(user);

        product = productRepository.save(product);

        return convertToDto(product); // Suponiendo que tienes un constructor que mapea `Product` a `ProductRequestDTO`
    }

    public List<ProductResponseDTO> getAllProductsForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProviderException("Usuario no autenticado"));

        return productRepository.findAllByRegistratedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public void updateProduct(Long id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Producto no encontrado en la base de datos"));

        validateProductData(id, productRequest);


        updateEntity(product, productRequest);
        product.setUpdatedDate(LocalDate.now());

        productRepository.save(product);
    }

    private void validateProductData(Long id, ProductRequestDTO productRequest) {
        // Verificar si existe otro producto con el mismo código
        Optional<Product> existingProduct = productRepository.findByCode(productRequest.getCode());
        if (existingProduct.isPresent() && !existingProduct.get().getId().equals(id)) {
            throw new ProductException("Ya existe un producto con este código.");
        }
    }

    private void updateEntity(Product product, ProductRequestDTO productRequest) {
        product.setCode(productRequest.getCode());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPurchasePrice(productRequest.getPurchasePrice());
        product.setSalePrice(productRequest.getSalePrice());
        product.setStock(productRequest.getStock());
        product.setMinStock(productRequest.getMinStock());
        product.setCategory(productRequest.getCategory());
        if (productRequest.getProviderId() == null) {
            throw new ProductException("Debe seleccionar un proveedor. Si desea que su proveedor siga siendo el mismo, seleccione: " + product.getProvider().getBusinessName());
        }
        //  Buscar proveedor
        Provider provider = providerRepository.findById(productRequest.getProviderId())
                .orElseThrow(() -> new ProductException("Proveedor no encontrado"));
        product.setProvider(provider);


        //  Buscar o crear la marca
        Brand brand = brandRepository.findByName(productRequest.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(productRequest.getBrandName());
                    return brandRepository.save(newBrand);
                });

        product.setBrand(brand);
    }

    public void updateStock(Long id, ProductUpdateStockDTO productUpdateStockDTO) {
        Product product = productRepository.findById(id).get();
        product.setStock(productUpdateStockDTO.getStock());
        productRepository.save(product);
    }

    @Transactional
    public void modifyProductPrices(UpdatePriceDTO updatePriceDTO) {
        List<Product> products = productRepository.findAllById(updatePriceDTO.getProductIds());

        if (products.isEmpty()) {
            throw new RuntimeException("No se encontraron productos");
        }

        double factor = 1 + (updatePriceDTO.getPercentage() / 100);
        if ("discount".equals(updatePriceDTO.getAction())) {
            factor = 1 - (updatePriceDTO.getPercentage() / 100);
        }

        for (Product product : products) {
            if ("purchasePrice".equals(updatePriceDTO.getPriceType())) {
                BigDecimal newPrice = (product.getPurchasePrice().multiply(BigDecimal.valueOf(parseDoubleTwoDecimals(factor))));
                product.setPurchasePrice(newPrice);
            } else if ("salePrice".equals(updatePriceDTO.getPriceType())) {
                BigDecimal newPrice = (product.getSalePrice().multiply(BigDecimal.valueOf(parseDoubleTwoDecimals(factor))));
                product.setSalePrice(newPrice);
            } else {
                throw new IllegalArgumentException("Tipo de precio no válido");
            }
        }

        productRepository.saveAll(products);
    }

    private double parseDoubleTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}