package co.ucentral.gestor.tareas.metodologias.servicios;

import co.ucentral.gestor.tareas.metodologias.dto.TaskDTO;
import co.ucentral.gestor.tareas.metodologias.dto.TaskResponseDTO;
import co.ucentral.gestor.tareas.metodologias.mappers.TaskMapper;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Task;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.TaskRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.TaskSpecification;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
    }

    public TaskResponseDTO createTask(TaskDTO taskDTO, String username) {
        Task task = taskMapper.toEntity(taskDTO);
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.isClosed()) {
            throw new IllegalArgumentException("Cannot delete active task");
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

    public List<TaskResponseDTO> getProjectTasks(Project projectId) {
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
}