package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

@Data
public class UserSettingsDTO {
    private Long userId;
    private String email;
    private String username;
    private String fullName;
    private String phoneNumber;
    private boolean isAdmin;
    private NotificationPreferencesDTO notifications;
}
