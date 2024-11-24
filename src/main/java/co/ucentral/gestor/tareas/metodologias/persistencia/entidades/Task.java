package co.ucentral.gestor.tareas.metodologias.persistencia.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@ToString(exclude = {"project", "assignedUsers"})
@EqualsAndHashCode(of = "id")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private boolean closed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "task_assigned_users",
            joinColumns = @JoinColumn(name = "tasks_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> assignedUsers = new ArrayList<>();

    @Version
    private Long version;

    // Helper methods
    public boolean isOverdue() {
        return !closed && dueDate.isBefore(LocalDateTime.now());
    }

    public void addAssignedUser(User user) {
        if (assignedUsers == null) {
            assignedUsers = new ArrayList<>();
        }
        assignedUsers.add(user);
    }

    public void removeAssignedUser(User user) {
        if (assignedUsers != null) {
            assignedUsers.remove(user);
        }
    }
}