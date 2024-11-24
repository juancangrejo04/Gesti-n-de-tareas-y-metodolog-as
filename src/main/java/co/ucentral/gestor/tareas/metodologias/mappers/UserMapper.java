package co.ucentral.gestor.tareas.metodologias.mappers;

import co.ucentral.gestor.tareas.metodologias.dto.UserDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserResponseDTO;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.isAdmin();
        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAdmin(user.isAdmin());
        return dto;
    }
}