package co.ucentral.gestor.tareas.metodologias.servicios;

import co.ucentral.gestor.tareas.metodologias.dto.DashboardDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectStatisticsDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserResponseDTO;
import co.ucentral.gestor.tareas.metodologias.mappers.ProjectMapper;
import co.ucentral.gestor.tareas.metodologias.mappers.TaskMapper;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Task;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.ProjectRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.TaskRepository;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    public DashboardDTO getDashboardData(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setUser(new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.isAdmin()
        ));

        List<Project> pendingProjects = projectRepository.findByCreatorAndClosedFalse(user);
        dashboard.setPendingProjects(pendingProjects.stream()
                .map(projectMapper::toResponseDTO)
                .collect(Collectors.toList()));

        List<Task> pendingTasks = taskRepository.findByAssignedUsersContainingAndClosedFalse(user);
        dashboard.setPendingTasks(pendingTasks.stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList()));

        dashboard.setStatistics(calculateStatistics(user));

        return dashboard;
    }

    private ProjectStatisticsDTO calculateStatistics(User user) {
        List<Project> allProjects = projectRepository.findByCreatorOrAssignedUsersContaining(user, user);

        ProjectStatisticsDTO stats = new ProjectStatisticsDTO();
        stats.setTotalProjects(allProjects.size());
        stats.setPendingProjects((int) allProjects.stream()
                .filter(p -> !p.isClosed()).count());
        stats.setCompletedProjects((int) allProjects.stream()
                .filter(Project::isClosed).count());
        stats.setOverdueProjects((int) allProjects.stream()
                .filter(p -> !p.isClosed() && p.getDueDate().isBefore(LocalDateTime.now()))
                .count());

        if (!allProjects.isEmpty()) {
            stats.setCompletionRate((double) stats.getCompletedProjects() / stats.getTotalProjects() * 100);
        }

        return stats;
    }
}