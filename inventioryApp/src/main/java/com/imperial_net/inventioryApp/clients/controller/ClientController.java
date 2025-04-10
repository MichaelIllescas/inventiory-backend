/**
 * Controlador REST encargado de gestionar las operaciones relacionadas con los clientes,
 * incluyendo registro, actualización, obtención y cambio de estado.
 */
package com.imperial_net.inventioryApp.clients.controller;

import com.imperial_net.inventioryApp.clients.dto.ClientRequestDTO;
import com.imperial_net.inventioryApp.clients.dto.ClientResponseDTO;
import com.imperial_net.inventioryApp.clients.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * Registra un nuevo cliente asociado al usuario autenticado.
     *
     * @param clientDto datos del cliente a registrar
     * @param request   petición HTTP que contiene la sesión del usuario
     * @return cliente registrado en formato DTO
     */
    @PostMapping("/clientRegister")
    public ResponseEntity<ClientResponseDTO> registerClient(
            @Valid @RequestBody ClientRequestDTO clientDto, HttpServletRequest request) {

        ClientResponseDTO savedClient = clientService.registerClient(clientDto, request);
        return ResponseEntity.ok(savedClient);
    }

    /**
     * Obtiene todos los clientes asociados al usuario autenticado.
     *
     * @param request petición HTTP con la sesión del usuario
     * @return lista de clientes
     */
    @GetMapping("/getClients")
    public ResponseEntity<?> getClients(HttpServletRequest request) {
        return ResponseEntity.ok(clientService.getAllClientsForUser(request));
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id            ID del cliente a actualizar
     * @param clientRequest datos nuevos del cliente
     * @param request       petición HTTP con la sesión del usuario
     * @return mensaje de confirmación
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(
            @PathVariable Long id, @RequestBody ClientRequestDTO clientRequest, HttpServletRequest request) {
        clientService.updateClient(id, clientRequest, request);
        return ResponseEntity.status(HttpStatus.OK).body("Cliente actualizado correctamente");
    }

    /**
     * Cambia el estado (activo/inactivo) de un cliente.
     *
     * @param id ID del cliente
     * @return mensaje de confirmación o error en caso de fallo
     */
    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<?> updateState(@PathVariable Long id) {
        try {
            clientService.toggleClientStatus(id);
            return ResponseEntity.ok("Estado actualizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el estado del cliente");
        }
    }
}