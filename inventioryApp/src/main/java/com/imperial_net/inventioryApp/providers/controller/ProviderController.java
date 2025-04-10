package com.imperial_net.inventioryApp.providers.controller;

import com.imperial_net.inventioryApp.providers.dto.ProviderRequestDTO;
import com.imperial_net.inventioryApp.providers.dto.ProviderResponseDTO;
import com.imperial_net.inventioryApp.providers.service.ProviderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador que gestiona las operaciones relacionadas con proveedores.
 * Permite registrar, listar, actualizar y cambiar el estado de proveedores.
 */
@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    /**
     * Registra un nuevo proveedor.
     *
     * @param providerDto datos del proveedor a registrar.
     * @param request     petición HTTP con cookies.
     * @return DTO del proveedor registrado.
     */
    @PostMapping("/providerRegister")
    public ResponseEntity<ProviderResponseDTO> registerProvider(
            @Valid @RequestBody ProviderRequestDTO providerDto, HttpServletRequest request) {

        ProviderResponseDTO savedProvider = providerService.registerProvider(providerDto, request);
        return ResponseEntity.ok(savedProvider);
    }

    /**
     * Obtiene todos los proveedores del usuario autenticado.
     *
     * @param request petición HTTP con cookies.
     * @return lista de proveedores.
     */
    @GetMapping("/getProviders")
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        return ResponseEntity.ok(providerService.getAllProvidersForUser(request));
    }

    /**
     * Obtiene todos los proveedores activos del usuario autenticado.
     *
     * @param request petición HTTP con cookies.
     * @return lista de proveedores activos.
     */
    @GetMapping("/getProvidersActive")
    public ResponseEntity<?> getProvidersActive(HttpServletRequest request) {
        return ResponseEntity.ok(providerService.getAllProvidersActiveForUser(request));
    }

    /**
     * Actualiza los datos de un proveedor existente.
     *
     * @param id              ID del proveedor a actualizar.
     * @param providerRequest nuevos datos del proveedor.
     * @return mensaje de éxito.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(
            @PathVariable Long id, @RequestBody ProviderRequestDTO providerRequest) {

        providerService.updateProvider(id, providerRequest);
        return ResponseEntity.status(HttpStatus.OK).body("usuario actualizado correctamente");
    }

    /**
     * Cambia el estado (activo/inactivo) de un proveedor.
     *
     * @param id ID del proveedor.
     * @return mensaje de éxito o error.
     */
    @PatchMapping("/toggleStatus/{id}")
    public ResponseEntity<?> updateState(@PathVariable Long id) {
        try {
            providerService.updateState(id);
            return ResponseEntity.ok("Estado actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar los precios");
        }
    }
}
