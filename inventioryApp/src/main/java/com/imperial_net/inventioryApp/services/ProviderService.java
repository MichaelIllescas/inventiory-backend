package com.imperial_net.inventioryApp.services;

import com.imperial_net.inventioryApp.dto.ProviderRequestDTO;
import com.imperial_net.inventioryApp.dto.ProviderResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProviderException;
import com.imperial_net.inventioryApp.models.Provider;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.ProviderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final CookieService cookieService;

    public ProviderResponseDTO registerProvider(ProviderRequestDTO providerDto, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProviderException("Usuario no autenticado. No se puede registrar el proveedor."));

        if (providerRepository.findByName(providerDto.getName()).isPresent()) {
            throw new ProviderException("El nombre del proveedor '" + providerDto.getName() + "' ya está registrado.");
        }

        Provider provider = convertToEntity(providerDto);
        provider.setCreatedBy(user);

        Provider savedProvider = providerRepository.save(provider);
        return convertToDto(savedProvider);
    }

    public List<ProviderResponseDTO> getAllProvidersForUser(HttpServletRequest request) {
        Long userId = cookieService.getUserFromCookie(request)
                .map(User::getId)
                .orElseThrow(() -> new ProviderException("Usuario no autenticado"));

        return providerRepository.findAllByCreatedBy_Id(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    public void updateProvider(Long id, ProviderRequestDTO providerRequest) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ProviderException("Proveedor no encontrado en la base de datos"));

        validateProviderData(id, providerRequest);

        updateEntity(provider, providerRequest);
        provider.setUpdateDate(LocalDate.now());

        providerRepository.save(provider);
    }

    private void validateProviderData(Long id, ProviderRequestDTO providerRequest) {
        providerRepository.findByName(providerRequest.getName())
                .filter(existingProvider -> !existingProvider.getId().equals(id))
                .ifPresent(p -> { throw new ProviderException("El nombre del proveedor '" + providerRequest.getName() + "' ya está registrado."); });

        providerRepository.findByTaxId(providerRequest.getTaxId())
                .filter(existingProvider -> !existingProvider.getId().equals(id))
                .ifPresent(p -> { throw new ProviderException("El CUIT/CUIL " + providerRequest.getTaxId() + " ya está registrado."); });
    }

    //  Método para convertir de Entidad a DTO
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
        dto.setCreatedBy(provider.getCreatedBy() != null ? provider.getCreatedBy().getFirstName() + " " + provider.getCreatedBy().getLastName() : null);
        dto.setRegistrationDate(provider.getRegistrationDate().format(formatter).toString());
        dto.setUpdateDate((provider.getUpdateDate() != null) ?provider.getUpdateDate().format(formatter).toString():null);
        return dto;
    }

    //  Método para convertir de DTO a Entidad
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

    // ✅ Método para actualizar una entidad existente con un DTO
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
}
