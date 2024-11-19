package co.ucentral.gestor.tareas.metodologias.dto;


import lombok.Data;

@Data
public class ProjectStatisticsDTO {
    private int totalProjects;
    private int pendingProjects;
    private int completedProjects;
    private int overdueProjects;
    private double completionRate;
}