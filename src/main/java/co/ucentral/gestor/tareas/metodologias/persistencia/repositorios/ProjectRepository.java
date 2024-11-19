package co.ucentral.gestor.tareas.metodologias.persistencia.repositorios;

import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Project;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatorAndClosedFalse(User creator);
    List<Project> findByCreatorOrAssignedUsersContaining(User creator, User assignedUser);
    int countByCreatorAndClosed(User creator, boolean closed);
}