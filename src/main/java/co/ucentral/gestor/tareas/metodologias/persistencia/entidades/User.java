package co.ucentral.gestor.tareas.metodologias.persistencia.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@ToString(exclude = {"projects", "assignedProjects", "password"})
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;
    private String fullName;
    private String phoneNumber;
    private boolean isAdmin;

    @OneToMany(mappedBy = "creator")
    private List<Project> projects;

    @ManyToMany(mappedBy = "assignedUsers")
    private List<Project> assignedProjects;
}
