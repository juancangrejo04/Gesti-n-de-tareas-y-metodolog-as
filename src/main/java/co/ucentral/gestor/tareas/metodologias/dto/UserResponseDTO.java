package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private boolean isAdmin;

    public UserResponseDTO(Long id, String email, String username, String fullName, String phoneNumber, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.isAdmin = isAdmin;
    }

    public UserResponseDTO() {

    }
}
