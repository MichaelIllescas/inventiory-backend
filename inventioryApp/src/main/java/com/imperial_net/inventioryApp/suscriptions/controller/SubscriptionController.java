package com.imperial_net.inventioryApp.suscriptions.controller;

import com.imperial_net.inventioryApp.users.dto.UserRegisterFromLandigPageDTO;  // DTO para el registro de usuario
import com.imperial_net.inventioryApp.suscriptions.service.SuscriptionService;  // Servicio para gestionar suscripciones
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador que maneja las operaciones relacionadas con las suscripciones de los usuarios.
 * Permite registrar usuarios para diferentes tipos de suscripción (prueba gratuita o suscripción Pro).
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/subsciption")  // Ruta base para las operaciones relacionadas con suscripciones
public class SubscriptionController {

    private final SuscriptionService suscriptionService;  // Servicio para gestionar la lógica de suscripción

    /**
     * Registra un usuario para un período de prueba gratuito.
     *
     * @param userRegisterFreeTrialDTO DTO con los datos del usuario que se va a registrar.
     * @return ResponseEntity con un mensaje de éxito.
     */
    @PostMapping("/registerFreeTrialUSer")  // Ruta para registrar usuarios en la prueba gratuita
    public ResponseEntity<?> registerFreeTrialUser(@RequestBody UserRegisterFromLandigPageDTO userRegisterFreeTrialDTO){
        suscriptionService.registerFreeTrialUser(userRegisterFreeTrialDTO);  // Llama al servicio para registrar al usuario
        return ResponseEntity.ok("Usuario Registrado correctamente");  // Responde con un mensaje de éxito
    }

    /**
     * Registra un usuario con una suscripción Pro.
     *
     * @param userRegisterFreeTrialDTO DTO con los datos del usuario que se va a registrar.
     * @return ResponseEntity con un mensaje de éxito.
     */
    @PostMapping("/registerProUSer")  // Ruta para registrar usuarios en la suscripción Pro
    public ResponseEntity<?> registerProUser(@RequestBody UserRegisterFromLandigPageDTO userRegisterFreeTrialDTO){
        suscriptionService.registerProUser(userRegisterFreeTrialDTO);  // Llama al servicio para registrar al usuario
        return ResponseEntity.ok("Usuario Registrado correctamente");  // Responde con un mensaje de éxito
    }
}
