/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con las empresas.
 * Permite registrar, actualizar y obtener empresas asociadas a los usuarios.
 */
package com.imperial_net.inventioryApp.companies.service;

import com.imperial_net.inventioryApp.companies.dto.CompanyRequestDTO;
import com.imperial_net.inventioryApp.companies.dto.CompanyResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.companies.model.Company;
import com.imperial_net.inventioryApp.auth.service.CookieService;
import com.imperial_net.inventioryApp.users.model.User;
import com.imperial_net.inventioryApp.companies.repository.CompanyRepository;
import com.imperial_net.inventioryApp.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CookieService cookieService;

    /**
     * Registra una nueva empresa para el usuario autenticado.
     *
     * @param requestDTO datos de la empresa
     * @param request    petición HTTP para obtener la sesión
     * @return empresa creada en formato DTO
     */
    public CompanyResponseDTO createCompany(CompanyRequestDTO requestDTO, HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede registrar la compania."));

        if (companyRepository.existsByUser(user)) {
            throw new IllegalStateException("El usuario ya tiene una empresa registrada.");
        }

        Company company = toEntity(requestDTO, user);
        Company savedCompany = companyRepository.save(company);
        return toResponseDTO(savedCompany);
    }

    /**
     * Obtiene la empresa asociada al usuario autenticado.
     *
     * @param request petición HTTP con la cookie del usuario
     * @return empresa en formato DTO
     */
    public CompanyResponseDTO getCompanyByUser(HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado."));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("La empresa no fue encontrada para el usuario."));

        return toResponseDTO(company);
    }

    /**
     * Actualiza los datos de una empresa existente.
     *
     * @param id                 ID de la empresa
     * @param companyRequestDTO  nuevos datos de la empresa
     */
    public void updateCompany(Long id, CompanyRequestDTO companyRequestDTO) {
        Company company = companyRepository.findById(id).get();
        company.setName(companyRequestDTO.getName());
        company.setEmail(companyRequestDTO.getEmail());
        company.setBusinessAddress(companyRequestDTO.getBusinessAddress());
        company.setTaxIdentificationNumber(companyRequestDTO.getTaxIdentificationNumber());
        company.setPhone(companyRequestDTO.getPhone());
        companyRepository.save(company);
    }

    /**
     * Convierte un CompanyRequestDTO a una entidad Company.
     *
     * @param dto  datos de la empresa
     * @param user usuario asociado
     * @return entidad Company
     */
    private Company toEntity(CompanyRequestDTO dto, User user) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setBusinessAddress(dto.getBusinessAddress());
        company.setTaxIdentificationNumber(dto.getTaxIdentificationNumber());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        company.setUser(user);
        return company;
    }

    /**
     * Convierte una entidad Company a un CompanyResponseDTO.
     *
     * @param company entidad Company
     * @return objeto DTO con los datos de la empresa
     */
    private CompanyResponseDTO toResponseDTO(Company company) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new CompanyResponseDTO(
                company.getId(),
                company.getName(),
                company.getBusinessAddress(),
                company.getTaxIdentificationNumber(),
                company.getPhone(),
                company.getEmail(),
                company.getUser().getId(),
                company.getRegistrationDate().format(formatter),
                company.getUpdateDate() == null ? "" : company.getUpdateDate().format(formatter)
        );
    }
}