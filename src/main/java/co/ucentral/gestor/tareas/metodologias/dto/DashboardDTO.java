package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {
    private UserResponseDTO user;
    private List<ProjectResponseDTO> pendingProjects;
    private List<TaskResponseDTO> pendingTasks;
    private ProjectStatisticsDTO statistics;
}