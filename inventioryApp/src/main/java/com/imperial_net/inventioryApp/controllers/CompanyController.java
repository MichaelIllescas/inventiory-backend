package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.CompanyRequestDTO;
import com.imperial_net.inventioryApp.dto.CompanyResponseDTO;
import com.imperial_net.inventioryApp.dto.UserDTO;
import com.imperial_net.inventioryApp.services.CompanyService;
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

    // POST - Crear nueva empresa
    @PostMapping("/register")
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO companyDTO, HttpServletRequest request) {
        CompanyResponseDTO response = companyService.createCompany(companyDTO, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getCompany")
    public ResponseEntity<CompanyResponseDTO> getCompanyForUser (HttpServletRequest request) {
        CompanyResponseDTO responseDTO = companyService.getCompanyByUser(request);
        return ResponseEntity.ok(responseDTO);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateUser(
            @PathVariable Long id, @RequestBody CompanyRequestDTO companyRequestDTO
    ){
        companyService.updateCompany(id,companyRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Empresa actualizada correctamente");
    }

}
