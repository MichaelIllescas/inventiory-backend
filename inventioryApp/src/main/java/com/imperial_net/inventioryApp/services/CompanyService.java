package com.imperial_net.inventioryApp.services;


import com.imperial_net.inventioryApp.dto.CompanyRequestDTO;
import com.imperial_net.inventioryApp.dto.CompanyResponseDTO;
import com.imperial_net.inventioryApp.exceptions.ProductException;
import com.imperial_net.inventioryApp.models.Company;
import com.imperial_net.inventioryApp.models.User;
import com.imperial_net.inventioryApp.repositories.CompanyRepository;
import com.imperial_net.inventioryApp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private  final CookieService cookieService;

    // Crear empresa
    public CompanyResponseDTO createCompany(CompanyRequestDTO requestDTO, HttpServletRequest request) {
        // Obtener usuario autenticado desde la cookie
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado. No se puede registrar la compania."));


        // Verificar si el usuario ya tiene una empresa
        if (companyRepository.existsByUser(user)) {
            throw new IllegalStateException("El usuario ya tiene una empresa registrada.");
        }

        Company company = toEntity(requestDTO, user);
        Company savedCompany = companyRepository.save(company);
        return toResponseDTO(savedCompany);
    }

    // Obtener empresa por ID de usuario
    public CompanyResponseDTO getCompanyByUser(HttpServletRequest request) {
        User user = cookieService.getUserFromCookie(request)
                .orElseThrow(() -> new ProductException("Usuario no autenticado."));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("La empresa no fue encontrada para el usuario."));

        return toResponseDTO(company);
    }

    // ==============================
    // === Métodos de conversión ====
    // ==============================

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
                company.getUpdateDate()==null? "" :company.getUpdateDate().format(formatter)
        );
    }

    public void updateCompany(Long id, CompanyRequestDTO companyRequestDTO) {
        Company company = companyRepository.findById(id).get();
        company.setName(companyRequestDTO.getName());
        company.setEmail(companyRequestDTO.getEmail());
        company.setBusinessAddress(companyRequestDTO.getBusinessAddress());
        company.setTaxIdentificationNumber(companyRequestDTO.getTaxIdentificationNumber());
        company.setPhone(companyRequestDTO.getPhone());
        companyRepository.save(company);


    }
}
