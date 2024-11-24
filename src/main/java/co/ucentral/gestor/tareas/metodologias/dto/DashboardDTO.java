package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {
    private int totalProjects;
    private int completedProjects;
    private int pendingProjects;
    private int overdueProjects;
    private List<ProjectResponseDTO> projects;
    private List<TaskResponseDTO> tasks;
}