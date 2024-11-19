package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDateTime dueDate;

    private Long projectId;
    private List<Long> assignedUserIds;
}