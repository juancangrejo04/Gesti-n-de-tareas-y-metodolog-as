package co.ucentral.gestor.tareas.metodologias.controladores;

import co.ucentral.gestor.tareas.metodologias.dto.ProjectBoardDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectDTO;
import co.ucentral.gestor.tareas.metodologias.dto.ProjectResponseDTO;
import co.ucentral.gestor.tareas.metodologias.servicios.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/board")
    public ResponseEntity<ProjectBoardDTO> getProjectBoard(Principal principal) {
        return ResponseEntity.ok(projectService.getProjectBoard(principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id, Principal principal) {
        try {
            projectService.deleteProject(id, principal.getName());
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (InvalidOperationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(
            @Valid @RequestBody ProjectDTO projectDTO,
            Principal principal) {
        return ResponseEntity.ok(projectService.createProject(projectDTO, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getUserProjects(Principal principal) {
        try {
            List<ProjectResponseDTO> projects = projectService.getUserProjects(principal.getName());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateProjectStatus(
            @PathVariable Long id,
            @RequestBody ProjectResponseDTO statusDTO,
            Principal principal) {
        try {
            projectService.updateProjectStatus(id, statusDTO.isClosed(), principal.getName());
            return ResponseEntity.ok().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al actualizar el estado del proyecto"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO,
            Principal principal) {
        try {
            ProjectResponseDTO updatedProject = projectService.updateProject(id, projectDTO, principal.getName());
            return ResponseEntity.ok(updatedProject);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al actualizar el proyecto"));
        }
    }


}
