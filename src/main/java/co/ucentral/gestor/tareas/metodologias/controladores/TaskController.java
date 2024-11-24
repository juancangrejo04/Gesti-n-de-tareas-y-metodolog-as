package co.ucentral.gestor.tareas.metodologias.controladores;

import co.ucentral.gestor.tareas.metodologias.dto.TaskDTO;
import co.ucentral.gestor.tareas.metodologias.dto.TaskResponseDTO;
import co.ucentral.gestor.tareas.metodologias.excepciones.TaskException;
import co.ucentral.gestor.tareas.metodologias.servicios.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
        try {
            TaskResponseDTO createdTask = taskService.createTask(taskDTO, principal.getName());
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }}

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
            @PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getProjectTasks(projectId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> status,
            Principal principal) {
        try {
            Boolean closed = status.get("closed");
            if (closed == null) {
                return ResponseEntity.badRequest().build();
            }
            TaskResponseDTO updatedTask = taskService.updateTaskStatus(id, closed, principal.getName());
            return ResponseEntity.ok(updatedTask);
        } catch (TaskException.TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (TaskException.UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (TaskException.IllegalTaskStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable Long id, Principal principal) {
        try {
            TaskResponseDTO task = taskService.getTask(id, principal.getName());
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}