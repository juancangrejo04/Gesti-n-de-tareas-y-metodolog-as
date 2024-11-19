package co.ucentral.gestor.tareas.metodologias.controladores;

import co.ucentral.gestor.tareas.metodologias.dto.DashboardDTO;
import co.ucentral.gestor.tareas.metodologias.servicios.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard(Principal principal) {
        return ResponseEntity.ok(dashboardService.getDashboardData(principal.getName()));
    }
}