package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private boolean isClosed;
    private UserResponseDTO creator;
    private List<UserResponseDTO> assignedUsers;
    private int totalTasks;
    private int completedTasks;
}