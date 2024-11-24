package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectColumnDTO {
    private String status;
    private List<ProjectResponseDTO> projects;
}
