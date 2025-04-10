/**
 * Controlador REST para el dashboard principal de la aplicación.
 * Proporciona datos estadísticos o resumen de información al usuario autenticado.
 */
package com.imperial_net.inventioryApp.dashboard.controller;

import com.imperial_net.inventioryApp.dashboard.dto.DashboardResponseDTO;
import com.imperial_net.inventioryApp.dashboard.service.DashboarService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboarService dashboarService;

    /**
     * Obtiene los datos del dashboard para el usuario autenticado.
     *
     * @param request petición HTTP que contiene la cookie del usuario
     * @return objeto con información del dashboard
     */
    @GetMapping("/data")
    public ResponseEntity<DashboardResponseDTO> getDashboard(HttpServletRequest request) {
        DashboardResponseDTO data = dashboarService.getDashboardData(request);
        return ResponseEntity.ok(data);
    }
}