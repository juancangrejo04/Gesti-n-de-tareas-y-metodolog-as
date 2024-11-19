package co.ucentral.gestor.tareas.metodologias.controladores;

import co.ucentral.gestor.tareas.metodologias.dto.TaskDTO;
import co.ucentral.gestor.tareas.metodologias.dto.TaskResponseDTO;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.servicios.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskDTO taskDTO,
            Principal principal) {
        return ResponseEntity.ok(taskService.createTask(taskDTO, principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO,
            Principal principal) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            Principal principal) {
        taskService.deleteTask(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TaskResponseDTO>> getPendingTasks(Principal principal) {
        return ResponseEntity.ok(taskService.getPendingTasks(principal.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDTO>> searchTasks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            Principal principal) {
        return ResponseEntity.ok(taskService.searchTasks(name, projectId, dateFrom, dateTo, principal.getName()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponseDTO>> getProjectTasks(
            @PathVariable Project projectId) {
        return ResponseEntity.ok(taskService.getProjectTasks(projectId));
    }
}