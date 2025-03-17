package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.ClientRequestDTO;
import com.imperial_net.inventioryApp.dto.ClientResponseDTO;
import com.imperial_net.inventioryApp.services.ClientService;
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

    @PostMapping("/clientRegister")
    public ResponseEntity<ClientResponseDTO> registerClient(
            @Valid @RequestBody ClientRequestDTO clientDto, HttpServletRequest request) {

        ClientResponseDTO savedClient = clientService.registerClient(clientDto, request);
        return ResponseEntity.ok(savedClient);
    }

        @GetMapping("/getClients")
    public ResponseEntity<?> getClients(HttpServletRequest request) {
        return ResponseEntity.ok(clientService.getAllClientsForUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(
            @PathVariable Long id, @RequestBody ClientRequestDTO clientRequest) {
        clientService.updateClient(id, clientRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Cliente actualizado correctamente");
    }

    @PatchMapping("/toggle-status/{id}")
    public ResponseEntity<?>updateState(
            @PathVariable Long id){
        try {
            clientService.toggleClientStatus(id);
            return ResponseEntity.ok("Estado actualziado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el estado del cliente");
        }
    }


}
