package com.imperial_net.inventioryApp.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio responsable de enviar correos electrónicos simples.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    /**
     * Componente de Spring encargado del envío de correos.
     */
    private final JavaMailSender mailSender;

    /**
     * Envía un correo electrónico con asunto y contenido especificados.
     *
     * @param to      dirección de correo electrónico del destinatario.
     * @param subject asunto del correo.
     * @param content contenido del mensaje.
     */
    public void send(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom("info@imperial-net.com");

        mailSender.send(message);
    }
}
