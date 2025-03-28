package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-email")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

    @GetMapping
    public String sendTestEmail() {
        emailService.send("joni.illes@hotmail.com", "Correo de prueba", "¡Funciona el envío desde Hostinger!");
        return "Correo enviado";
    }
}
