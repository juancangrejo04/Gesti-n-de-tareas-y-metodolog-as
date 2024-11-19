package co.ucentral.gestor.tareas.metodologias.persistencia.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "projects")
public class Project {
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
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "project_users",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> assignedUsers = new ArrayList<>();

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Task> tasks = new ArrayList<>();

    @Version
    private Long version;

    // Helper methods
    public boolean isOverdue() {
        return !closed && dueDate.isBefore(LocalDateTime.now());
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
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