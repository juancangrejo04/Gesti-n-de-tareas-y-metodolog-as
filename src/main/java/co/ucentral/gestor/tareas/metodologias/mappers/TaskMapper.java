package co.ucentral.gestor.tareas.metodologias.mappers;

import co.ucentral.gestor.tareas.metodologias.dto.TaskDTO;
import co.ucentral.gestor.tareas.metodologias.dto.TaskResponseDTO;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Task;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    private final UserMapper userMapper;

    @Autowired
    public TaskMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Task toEntity(TaskDTO dto) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());

        if (dto.getProjectId() != null) {

            Project project = new Project();
            project.setId(dto.getProjectId());
            task.setProject(project);
        }

        return task;
    }


    public TaskResponseDTO toResponseDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setCreationDate(task.getCreationDate());
        dto.setDueDate(task.getDueDate());
        dto.setClosed(task.isClosed());



        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
        }

        if (task.getAssignedUsers() != null) {
            dto.setAssignedUsers(task.getAssignedUsers().stream()
                    .map(userMapper::toResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

}
