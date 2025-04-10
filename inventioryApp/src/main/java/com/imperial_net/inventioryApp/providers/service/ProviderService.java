package com.imperial_net.inventioryApp.providers.service;

import com.imperial_net.inventioryApp.providers.dto.ProviderRequestDTO;
import com.imperial_net.inventioryApp.providers.dto.ProviderResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.exceptions.ProviderException;
import com.imperial_net.inventioryApp.providers.model.Provider;
import com.imperial_net.inventioryApp.providers.repository.ProviderRepository;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.suscriptions.model.Subscription;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio encargado de la lógica de negocio relacionada con proveedores.
 */
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final CookieService cookieService;

    /**
     * Registra un nuevo proveedor asociado al usuario autenticado.
     */
    public ProviderResponseDTO registerProvider(ProviderRequestDTO providerDto, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProviderException("Usuario no autenticado. No se puede registrar el proveedor."));

        if (providerRepository.findByName(providerDto.getName()).isPresent()) {
            throw new ProviderException("El nombre del proveedor '" + providerDto.getName() + "' ya está registrado.");
        }

        Provider provider = convertToEntity(providerDto);
        provider.setCreatedBy(user);

        if (this.validateNumberOfRecords(user)) {
            Provider savedProvider = providerRepository.save(provider);
            return convertToDto(savedProvider);
        } else {
            throw new ProviderException("Ha alcanzado el límite de registros para el plan FREE. Si desea acceder a registros ilimitados, debe suscribirse al plan PRO");
        }
    }

    /**
     * Devuelve todos los proveedores registrados por el usuario autenticado.
     */
    public List<ProviderResponseDTO> getAllProvidersForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProviderException("Usuario no autenticado"));

        return providerRepository.findAllByCreatedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Actualiza los datos de un proveedor existente.
     */
    public void updateProvider(Long id, ProviderRequestDTO providerRequest) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ProviderException("Proveedor no encontrado en la base de datos"));

        validateProviderData(id, providerRequest);
        updateEntity(provider, providerRequest);
        provider.setUpdateDate(LocalDate.now());

        providerRepository.save(provider);
    }

    /**
     * Valida que no exista otro proveedor con el mismo nombre o CUIT/CUIL.
     */
    private void validateProviderData(Long id, ProviderRequestDTO providerRequest) {
        providerRepository.findByName(providerRequest.getName())
                .filter(existingProvider -> !existingProvider.getId().equals(id))
                .ifPresent(p -> {
                    throw new ProviderException("El nombre del proveedor '" + providerRequest.getName() + "' ya está registrado.");
                });

        providerRepository.findByTaxId(providerRequest.getTaxId())
                .filter(existingProvider -> !existingProvider.getId().equals(id))
                .ifPresent(p -> {
                    throw new ProviderException("El CUIT/CUIL " + providerRequest.getTaxId() + " ya está registrado.");
                });
    }

    /**
     * Convierte una entidad {@link Provider} a su representación DTO.
     */
    public ProviderResponseDTO convertToDto(Provider provider) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        ProviderResponseDTO dto = new ProviderResponseDTO();
        dto.setId(provider.getId());
        dto.setName(provider.getName());
        dto.setBusinessName(provider.getBusinessName());
        dto.setTaxId(provider.getTaxId());
        dto.setEmail(provider.getEmail());
        dto.setPhone(provider.getPhone());
        dto.setAddress(provider.getAddress());
        dto.setWebsite(provider.getWebsite());
        dto.setContactPerson(provider.getContactPerson());
        dto.setNotes(provider.getNotes());
        dto.setCreatedBy(provider.getCreatedBy() != null
                ? provider.getCreatedBy().getFirstName() + " " + provider.getCreatedBy().getLastName()
                : null);
        dto.setRegistrationDate(provider.getRegistrationDate().format(formatter));
        dto.setUpdateDate(provider.getUpdateDate() != null
                ? provider.getUpdateDate().format(formatter)
                : null);
        dto.setState((provider.getState()) ? "ACTIVO" : "INACTIVO");
        return dto;
    }

    /**
     * Convierte un DTO {@link ProviderRequestDTO} a entidad {@link Provider}.
     */
    public Provider convertToEntity(ProviderRequestDTO dto) {
        Provider provider = new Provider();
        provider.setName(dto.getName());
        provider.setBusinessName(dto.getBusinessName());
        provider.setTaxId(dto.getTaxId());
        provider.setEmail(dto.getEmail());
        provider.setPhone(dto.getPhone());
        provider.setAddress(dto.getAddress());
        provider.setWebsite(dto.getWebsite());
        provider.setContactPerson(dto.getContactPerson());
        provider.setNotes(dto.getNotes());
        return provider;
    }

    /**
     * Actualiza los campos de una entidad {@link Provider} con los valores del DTO.
     */
    private void updateEntity(Provider provider, ProviderRequestDTO dto) {
        provider.setName(dto.getName());
        provider.setBusinessName(dto.getBusinessName());
        provider.setTaxId(dto.getTaxId());
        provider.setEmail(dto.getEmail());
        provider.setPhone(dto.getPhone());
        provider.setAddress(dto.getAddress());
        provider.setWebsite(dto.getWebsite());
        provider.setContactPerson(dto.getContactPerson());
        provider.setNotes(dto.getNotes());
    }

    /**
     * Cambia el estado (activo/inactivo) de un proveedor.
     */
    public void updateState(Long id) {
        Provider provider = providerRepository.findById(id).get();
        if (provider.getState()) {
            provider.setState(false);
        } else {
            provider.setState(true);
        }
        providerRepository.save(provider);
    }

    /**
     * Obtiene todos los proveedores activos del usuario autenticado.
     */
    public List<ProviderResponseDTO> getAllProvidersActiveForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProductException("Usuario no autenticado"));

        return providerRepository.findAllByCreatedBy_IdAndStateTrue(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Obtiene un proveedor por su ID.
     */
    public Provider getProviderById(Long providerId) {
        return providerRepository.findById(providerId).get();
    }

    /**
     * Valida si el usuario puede seguir registrando proveedores según su plan.
     */
    public boolean validateNumberOfRecords(User user) {
        if (user.getSubscription() == Subscription.FREE) {
            return providerRepository.countByCreatedBy_Id(user.getId()) < 10;
        }
        return true;
    }
}
