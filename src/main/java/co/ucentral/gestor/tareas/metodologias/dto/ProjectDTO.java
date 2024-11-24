package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDateTime dueDate;

    private List<Long> assignedUserIds;
    private boolean isClosed;
}