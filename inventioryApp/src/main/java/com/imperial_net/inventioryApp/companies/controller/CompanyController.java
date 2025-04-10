/**
 * Controlador REST que maneja las operaciones relacionadas con la entidad Empresa (Company).
 * Permite registrar, obtener y actualizar información de una empresa.
 */
package com.imperial_net.inventioryApp.companies.controller;

import com.imperial_net.inventioryApp.companies.dto.CompanyRequestDTO;
import com.imperial_net.inventioryApp.companies.dto.CompanyResponseDTO;
import com.imperial_net.inventioryApp.companies.service.CompanyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Registra una nueva empresa asociada al usuario autenticado.
     *
     * @param companyDTO datos de la empresa a registrar
     * @param request    petición HTTP que contiene la sesión del usuario
     * @return la empresa creada en formato DTO
     */
    @PostMapping("/register")
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO companyDTO, HttpServletRequest request) {
        CompanyResponseDTO response = companyService.createCompany(companyDTO, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene la empresa asociada al usuario autenticado.
     *
     * @param request petición HTTP que contiene la sesión del usuario
     * @return empresa del usuario en formato DTO
     */
    @GetMapping("/getCompany")
    public ResponseEntity<CompanyResponseDTO> getCompanyForUser(HttpServletRequest request) {
        CompanyResponseDTO responseDTO = companyService.getCompanyByUser(request);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Actualiza los datos de una empresa existente.
     *
     * @param id                ID de la empresa a actualizar
     * @param companyRequestDTO nuevos datos de la empresa
     * @return mensaje de confirmación
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody CompanyRequestDTO companyRequestDTO) {
        companyService.updateCompany(id, companyRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Empresa actualizada correctamente");
    }
}