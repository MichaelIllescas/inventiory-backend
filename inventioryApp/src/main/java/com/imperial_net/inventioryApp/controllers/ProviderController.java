    package com.imperial_net.inventioryApp.controllers;

    import com.imperial_net.inventioryApp.dto.ProviderRequestDTO;
    import com.imperial_net.inventioryApp.dto.ProviderResponseDTO;
    import com.imperial_net.inventioryApp.dto.UserDTO;
    import com.imperial_net.inventioryApp.models.Provider;
    import com.imperial_net.inventioryApp.services.ProviderService;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Optional;

    @RestController
    @RequestMapping("/providers")
    @RequiredArgsConstructor
    public class ProviderController {
        private final ProviderService providerService;

        @PostMapping("/providerRegister")
        public ResponseEntity<ProviderResponseDTO> registerProvider(
                @Valid @RequestBody ProviderRequestDTO providerDto, HttpServletRequest request) {

            ProviderResponseDTO savedProvider = providerService.registerProvider(providerDto, request);
            return ResponseEntity.ok(savedProvider);
        }


        @GetMapping("/getProviders")
        public ResponseEntity<?> getUsers(HttpServletRequest request) {
            return ResponseEntity.ok(providerService.getAllProvidersForUser(request));
        }

            @GetMapping("/getProvidersActive")
        public ResponseEntity<?> getProvidersActive(HttpServletRequest request) {
            return ResponseEntity.ok(providerService.getAllProvidersActiveForUser(request));
        }

        @PutMapping("/{id}")
        public ResponseEntity<?>updateProvider(
                @PathVariable Long id, @RequestBody ProviderRequestDTO providerRequest
        ){
            providerService.updateProvider(id,providerRequest);
            return ResponseEntity.status(HttpStatus.OK).body("usuario actualizado correctamente");
        }
        @PatchMapping("/toggleStatus/{id}")
        public ResponseEntity<?>updateState(
                @PathVariable Long id) {
            try {
                providerService.updateState(id);
                return ResponseEntity.ok("Estado actualziado correctamente");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar los precios");
            }

        }

    }
