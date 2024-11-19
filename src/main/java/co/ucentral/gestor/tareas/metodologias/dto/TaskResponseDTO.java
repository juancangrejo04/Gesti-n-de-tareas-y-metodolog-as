package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private boolean closed;
    private Long projectId;
    private List<UserResponseDTO> assignedUsers;
}
