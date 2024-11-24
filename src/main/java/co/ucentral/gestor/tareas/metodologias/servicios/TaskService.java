package co.ucentral.gestor.tareas.metodologias.servicios;

import co.ucentral.gestor.tareas.metodologias.controladores.UnauthorizedException;
import co.ucentral.gestor.tareas.metodologias.dto.TaskDTO;
import co.ucentral.gestor.tareas.metodologias.dto.TaskResponseDTO;
import co.ucentral.gestor.tareas.metodologias.mappers.TaskMapper;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Task;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.ProjectRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.TaskRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.TaskSpecification;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final ProjectRepository projectRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       TaskMapper taskMapper, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.projectRepository = projectRepository;
    }

    public TaskResponseDTO createTask(TaskDTO taskDTO, String username) {
        if (taskDTO.getProjectId() == null) {
            throw new IllegalArgumentException("El ID del proyecto es obligatorio");
        }

        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + taskDTO.getProjectId()));

        Task task = taskMapper.toEntity(taskDTO);
        task.setProject(project);
        task.setCreationDate(LocalDateTime.now());
        task.setAssignedUsers(getAssignedUsers(taskDTO.getAssignedUserIds()));

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);
    }


    public TaskResponseDTO updateTask(Long id, TaskDTO taskDTO, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setAssignedUsers(getAssignedUsers(taskDTO.getAssignedUserIds()));

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(updatedTask);
    }

    public void deleteTask(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!task.getProject().getCreator().equals(user) &&
                !task.getAssignedUsers().contains(user) &&
                !user.isAdmin()) {
            throw new UnauthorizedException("No tiene permisos para eliminar esta tarea");
        }

        taskRepository.deleteById(id);
    }

    public List<TaskResponseDTO> getPendingTasks(String username) {
        List<Task> tasks = taskRepository.findByClosed(false);
        return tasks.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> searchTasks(String name, Long projectId,
                                             String dateFrom, String dateTo,
                                             String username) {
        LocalDateTime startDate = parseDate(dateFrom);
        LocalDateTime endDate = parseDate(dateTo);

        Specification<Task> spec = Specification.where(TaskSpecification.hasName(name))
                .and(TaskSpecification.hasProject(projectId))
                .and(TaskSpecification.dueDateBetween(startDate, endDate));

        List<Task> tasks = taskRepository.findAll((Sort) spec);
        return tasks.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getProjectTasks(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private List<User> getAssignedUsers(List<Long> assignedUserIds) {
        if (assignedUserIds == null || assignedUserIds.isEmpty()) {
            return List.of();
        }

        return assignedUserIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId)))
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDate(String date) {
        try {
            return date != null ? LocalDateTime.parse(date) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    public TaskResponseDTO updateTaskStatus(Long id, Boolean closed, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getAssignedUsers().stream()
                .anyMatch(user -> user.getUsername().equals(username)) && !task.getProject().getCreator().getUsername().equals(username)) {
            throw new IllegalArgumentException("User not authorized to update task status");
        }
        task.setClosed(closed);
        Task updatedTask = taskRepository.save(task);

        return taskMapper.toResponseDTO(updatedTask);
    }

    public TaskResponseDTO getTask(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!task.getProject().getCreator().equals(user) &&
                !task.getAssignedUsers().contains(user) &&
                !user.isAdmin()) {
            throw new UnauthorizedException("No tiene permisos para ver esta tarea");
        }

        return taskMapper.toResponseDTO(task);
    }


}