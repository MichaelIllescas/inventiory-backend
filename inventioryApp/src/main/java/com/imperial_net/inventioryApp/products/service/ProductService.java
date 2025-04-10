package com.imperial_net.inventioryApp.products.service;

import com.imperial_net.inventioryApp.exceptions.ClientException;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.products.dto.*;
import com.imperial_net.inventioryApp.products.models.Brand;
import com.imperial_net.inventioryApp.products.models.Product;
import com.imperial_net.inventioryApp.products.repository.BrandRepository;
import com.imperial_net.inventioryApp.products.repository.ProductRepository;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona la lógica de negocio para productos.
 * Incluye registro, edición, modificación de precios y consultas.
 */
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CookieService cookieService;
    private final BrandRepository brandRepository;

    /**
     * Convierte un {@link Product} a {@link ProductResponseDTO}.
     */
    public ProductResponseDTO convertToDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSalePrice(product.getSalePrice());
        dto.setMinStock(product.getMinStock());
        dto.setCategory(product.getCategory());
        dto.setRegistratedByName(
                (product.getRegistratedBy() != null) ? product.getRegistratedBy().getUsername() : null
        );
        dto.setBrandName(
                (product.getBrand() != null) ? product.getBrand().getName() : null
        );
        dto.setRegistrationDate(
                product.getRegistrationDate() != null ? product.getRegistrationDate().toString() : null
        );
        dto.setUpdatedDate(
                product.getUpdatedDate() != null ? product.getUpdatedDate().toString() : null
        );
        dto.setStock(product.getStock());
        dto.setState((product.getState()) ? "ACTIVO" : "INACTIVO");
        return dto;
    }

    /**
     * Registra un nuevo producto en la base de datos.
     */
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO productRequestDTO, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede registrar el producto."));

        if (productRepository.existsByCodeAndRegistratedBy_Id(productRequestDTO.getCode(), user.getId())) {
            throw new ProductException("Ya existe un producto con el código '" + productRequestDTO.getCode() + "'.");
        }

        // Buscar o crear la marca
        Brand brand = brandRepository.findByName(productRequestDTO.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(productRequestDTO.getBrandName());
                    return brandRepository.save(newBrand);
                });

        // Crear y guardar producto
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

        if (this.validateNumberOfRecords(user)) {
            product = productRepository.save(product);
            return convertToDto(product);
        } else {
            throw new ClientException("Ha alcanzado el límite de registros para el plan FREE. Si desea acceder a registros ilimitados, debe suscribirse al plan PRO");
        }
    }

    /**
     * Obtiene todos los productos registrados por el usuario autenticado.
     */
    public List<ProductResponseDTO> getAllProductsForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return productRepository.findAllByRegistratedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Actualiza los datos de un producto por su ID.
     */
    public void updateProduct(Long id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Producto no encontrado en la base de datos"));

        validateProductData(id, productRequest);
        updateEntity(product, productRequest);
        product.setUpdatedDate(LocalDate.now());

        productRepository.save(product);
    }

    /**
     * Valida que no haya otro producto con el mismo código.
     */
    private void validateProductData(Long id, ProductRequestDTO productRequest) {
        Optional<Product> existingProduct = productRepository.findByCode(productRequest.getCode());
        if (existingProduct.isPresent() && !existingProduct.get().getId().equals(id)) {
            throw new ProductException("Ya existe un producto con este código.");
        }
    }

    /**
     * Aplica los datos del DTO a una entidad existente.
     */
    private void updateEntity(Product product, ProductRequestDTO productRequest) {
        product.setCode(productRequest.getCode());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setSalePrice(productRequest.getSalePrice());
        product.setMinStock(productRequest.getMinStock());
        product.setCategory(productRequest.getCategory());

        Brand brand = brandRepository.findByName(productRequest.getBrandName())
                .orElseGet(() -> {
                    Brand newBrand = new Brand();
                    newBrand.setName(productRequest.getBrandName());
                    return brandRepository.save(newBrand);
                });

        product.setBrand(brand);
    }

    /**
     * Modifica el precio de venta de productos en base a un porcentaje y acción.
     */
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

    /**
     * Redondea un número a dos decimales.
     */
    private double parseDoubleTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Actualiza el stock de un producto.
     */
    public void updateStock(Long id, ProductUpdateStockDTO productUpdateStockDTO) {
        Product product = productRepository.findById(id).get();
        product.setStock(productUpdateStockDTO.getStock());
        productRepository.save(product);
    }

    /**
     * Cambia el estado (activo/inactivo) de un producto.
     */
    public void updateState(Long id) {
        Product product = productRepository.findById(id).get();
        if ((product.getState())) {
            product.setState(false);
        } else {
            product.setState(true);
        }
        productRepository.save(product);
    }

    /**
     * Devuelve todos los productos activos del usuario autenticado.
     */
    public List<ProductResponseDTO> getAllProductsActiveForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return productRepository.findAllByRegistratedBy_IdAndStateTrue(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Devuelve productos con stock por debajo del mínimo.
     */
    public List<StockLowDTO> findLowStockProducts(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        List<Product> productsList = productRepository.findLowStockProductsByUser(userId);

        return productsList.stream()
                .map(product -> new StockLowDTO(
                        product.getCode(),
                        product.getName(),
                        product.getStock(),
                        product.getMinStock()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un producto por su ID.
     */
    public Product getProductById(Long productId) {
        return productRepository.findById(productId).get();
    }

    /**
     * Guarda directamente un producto (uso interno).
     */
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    /**
     * Busca un producto por su código y el usuario autenticado.
     */
    public Product getProductByCode(String productCode, HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return productRepository.findByCodeAndRegistratedBy_Id(productCode, userId).get();
    }

    /**
     * Valida si el usuario puede registrar más productos según su plan.
     */
    public boolean validateNumberOfRecords(User user) {
        if (user.getSubscription() == Subscription.FREE) {
            return productRepository.countByRegistratedBy_Id(user.getId()) < 10;
        }
        return true;
    }
}
