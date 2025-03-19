package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.*;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.models.Brand;
import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.BrandRepository;
import com.imperial_net.inventioryApp.repositories.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CookieService cookieService;
    private final BrandRepository brandRepository;

    public ProductResponseDTO convertToDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSalePrice(product.getSalePrice());
        dto.setMinStock(product.getMinStock());
        dto.setCategory(product.getCategory());
        dto.setRegistratedByName((product.getRegistratedBy() != null) ? product.getRegistratedBy().getUsername() : null);
        dto.setBrandName((product.getBrand() != null) ? product.getBrand().getName() : null);
        dto.setRegistrationDate(product.getRegistrationDate() != null ? product.getRegistrationDate().toString() : null);
        dto.setUpdatedDate(product.getUpdatedDate() != null ? product.getUpdatedDate().toString() : null);
        dto.setStock(product.getStock());
        dto.setState((product.getState())?"ACTIVO":"INACTIVO");
        return dto;
    }

    @Transactional
    public ProductResponseDTO save(ProductRequestDTO productRequestDTO, HttpServletRequest request) {

        // Obtener usuario autenticado desde la cookie
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede registrar el producto."));

        // Verificar si ya existe un producto con el mismo código
        if (productRepository.existsByCodeAndRegistratedBy_Id(productRequestDTO.getCode(), user.getId()) ){

            throw new ProductException("Ya existe un producto con el código '" + productRequestDTO.getCode() + "'.");
        }

        // Buscar o crear la marca
        Brand brand = brandRepository.findByName(productRequestDTO.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(productRequestDTO.getBrandName());
                    return brandRepository.save(newBrand);
                });

        // Crear y guardar el producto
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setCode(productRequestDTO.getCode());
        product.setBrand(brand);
        product.setDescription(productRequestDTO.getDescription());
        product.setSalePrice(productRequestDTO.getSalePrice());
        product.setMinStock(productRequestDTO.getMinStock());
        product.setCategory(productRequestDTO.getCategory());
        product.setRegistratedBy(user);
        product.setRegistrationDate(LocalDate.now());


        product = productRepository.save(product);

        return convertToDto(product);
    }

    public List<ProductResponseDTO> getAllProductsForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

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
        product.setSalePrice(productRequest.getSalePrice());
        product.setMinStock(productRequest.getMinStock());
        product.setCategory(productRequest.getCategory());

        // Buscar o crear la marca
        Brand brand = brandRepository.findByName(productRequest.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(productRequest.getBrandName());
                    return brandRepository.save(newBrand);
                });

        product.setBrand(brand);
    }

    @Transactional
    public void modifyProductPrices(UpdatePriceDTO updatePriceDTO) {
        List<Product> products = productRepository.findAllById(updatePriceDTO.getProductIds());

        if (products.isEmpty()) {
            throw new ProductException("No se encontraron productos.");
        }

        double factor = 1 + (updatePriceDTO.getPercentage() / 100);
        if ("discount".equals(updatePriceDTO.getAction())) {
            factor = 1 - (updatePriceDTO.getPercentage() / 100);
        }

        for (Product product : products) {
            if ("salePrice".equals(updatePriceDTO.getPriceType())) {
                BigDecimal newPrice = product.getSalePrice().multiply(BigDecimal.valueOf(parseDoubleTwoDecimals(factor)));
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

    public void updateStock(Long id, ProductUpdateStockDTO productUpdateStockDTO) {
        Product product = productRepository.findById(id).get();
        product.setStock(productUpdateStockDTO.getStock());
        productRepository.save(product);
    }

    public void updateState(Long id) {
        Product product = productRepository.findById(id).get();
        if ((product.getState())) {
            product.setState(false);
        } else {
            product.setState(true);
        }
        productRepository.save(product);

    }

    public List<ProductResponseDTO> getAllProductsActiveForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return productRepository.findAllByRegistratedBy_IdAndStateTrue(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<StockLowDTO> findLowStockProducts(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));
        List<Product> productsList= productRepository
                .findLowStockProductsByUser(userId);
      return  productsList.stream()
                .map(product -> new StockLowDTO(
                        product.getCode(),
                        product.getName(),
                        product.getStock(),
                        product.getMinStock()
                ))
                .collect(Collectors.toList());
    }

    public Product getProductById(Long productId) {
            return productRepository.findById(productId).get();
    }

    public void saveProduct(Product product) {
        productRepository.save(product);

    }


}
