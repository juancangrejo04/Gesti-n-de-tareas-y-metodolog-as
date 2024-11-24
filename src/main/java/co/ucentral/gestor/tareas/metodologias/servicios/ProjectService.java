package co.ucentral.gestor.tareas.metodologias.servicios;

import co.ucentral.gestor.tareas.metodologias.controladores.InvalidOperationException;
import co.ucentral.gestor.tareas.metodologias.controladores.UnauthorizedException;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectBoardDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectColumnDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectResponseDTO;
import co.ucentral.gestor.tareas.metodologias.mappers.ProjectMapper;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.ProjectRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.TaskRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponseDTO createProject(ProjectDTO projectDTO, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Project project = projectMapper.toEntity(projectDTO);
        project.setCreator(creator);
        project.setCreationDate(LocalDateTime.now());
        project.setClosed(false);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponseDTO(savedProject);
    }

    public List<ProjectResponseDTO> getUserProjects(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Project> projects = projectRepository.findByCreatorOrAssignedUsersContaining(user, user);
        return projects.stream()
                .map(project -> {
                    ProjectResponseDTO dto = projectMapper.toResponseDTO(project);
                    dto.setTotalTasks(taskRepository.countByProject(project));
                    dto.setCompletedTasks(taskRepository.countByProjectAndClosed(project, true));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteProject(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        if (hasPendingTasks(project) && !user.isAdmin()) {
            throw new InvalidOperationException(
                    "No se puede eliminar el proyecto porque tiene tareas pendientes");
        }
        projectRepository.delete(project);
    }

    private boolean hasPendingTasks(Project project) {
        return taskRepository.countByProjectAndClosed(project, false) > 0;
    }

    public ProjectBoardDTO getProjectBoard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Project> projects = projectRepository.findByCreatorOrAssignedUsersContaining(user, user);

        ProjectBoardDTO board = new ProjectBoardDTO();
        List<ProjectColumnDTO> columns = Arrays.asList(
                createColumn("Pendientes", projects.stream()
                        .filter(p -> !p.isClosed())
                        .collect(Collectors.toList())),
                createColumn("Completados", projects.stream()
                        .filter(Project::isClosed)
                        .collect(Collectors.toList()))
        );

        board.setColumns(columns);
        return board;
    }

    private ProjectColumnDTO createColumn(String status, List<Project> projects) {
        ProjectColumnDTO column = new ProjectColumnDTO();
        column.setStatus(status);
        column.setProjects(projects.stream()
                .map(projectMapper::toResponseDTO)
                .collect(Collectors.toList()));
        return column;
    }

    public void updateProjectStatus(Long projectId, boolean closed, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!project.getCreator().equals(user) && !user.isAdmin()) {
            throw new UnauthorizedException("No tiene permisos para actualizar este proyecto");
        }

        project.setClosed(closed);
        projectRepository.save(project);
    }

    public ProjectResponseDTO updateProject(Long id, ProjectDTO projectDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        if (!project.getCreator().equals(user) && !user.isAdmin()) {
            throw new UnauthorizedException("No tiene permisos para actualizar este proyecto");
        }

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setDueDate(projectDTO.getDueDate());

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponseDTO(updatedProject);
    }
}
