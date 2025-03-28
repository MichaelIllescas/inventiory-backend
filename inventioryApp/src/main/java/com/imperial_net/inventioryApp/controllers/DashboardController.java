package com.imperial_net.inventioryApp.controllers;

import com.imperial_net.inventioryApp.dto.DashboardResponseDTO;
import com.imperial_net.inventioryApp.services.DashboarService;
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

    @GetMapping("/data")
    public ResponseEntity<DashboardResponseDTO> getDashboard(HttpServletRequest request) {
        DashboardResponseDTO data = dashboarService.getDashboardData(request);
        return ResponseEntity.ok(data);
    }
}
