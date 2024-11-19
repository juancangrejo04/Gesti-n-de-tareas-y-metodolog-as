package co.ucentral.gestor.tareas.metodologias.dto;

import lombok.Data;

@Data
public class NotificationPreferencesDTO {
    private boolean emailNotifications;
    private boolean taskAssignedNotifications;
    private boolean projectUpdateNotifications;
    private boolean deadlineNotifications;
}