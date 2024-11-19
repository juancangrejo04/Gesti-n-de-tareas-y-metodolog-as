package co.ucentral.gestor.tareas.metodologias.mappers;

import co.ucentral.gestor.tareas.metodologias.dto.ProjectDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectResponseDTO;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    @Autowired
    private UserMapper userMapper;

    public ProjectResponseDTO toResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreationDate(project.getCreationDate());
        dto.setDueDate(project.getDueDate());
        dto.setClosed(project.isClosed());
        dto.setCreator(userMapper.toResponseDTO(project.getCreator()));
        dto.setAssignedUsers(project.getAssignedUsers().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList()));
        dto.setTotalTasks(project.getTasks().size());
        dto.setCompletedTasks((int) project.getTasks().stream()
                .filter(task -> task.isClosed())
                .count());
        return dto;
    }

    public Project toEntity(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setDueDate(projectDTO.getDueDate());
        project.setClosed(projectDTO.isClosed());
        return project;
    }
}
