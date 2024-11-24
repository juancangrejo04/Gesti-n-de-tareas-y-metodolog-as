package co.ucentral.gestor.tareas.metodologias.persistencia.repositorios;

import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Task;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class TaskSpecification {

    public static Specification<Task> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Task> hasProject(Long projectId) {
        return (root, query, cb) -> {
            if (projectId == null) {
                return null;
            }
            return cb.equal(root.get("projectId"), projectId);
        };
    }

    public static Specification<Task> dueDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return (root, query, cb) -> {
            if (dateFrom == null && dateTo == null) {
                return null;
            }
            if (dateFrom == null) {
                return cb.lessThanOrEqualTo(root.get("dueDate"), dateTo);
            }
            if (dateTo == null) {
                return cb.greaterThanOrEqualTo(root.get("dueDate"), dateFrom);
            }
            return cb.between(root.get("dueDate"), dateFrom, dateTo);
        };
    }
}