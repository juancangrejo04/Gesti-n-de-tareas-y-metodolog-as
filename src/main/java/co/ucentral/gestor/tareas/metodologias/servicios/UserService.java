package co.ucentral.gestor.tareas.metodologias.servicios;

import co.ucentral.gestor.tareas.metodologias.dto.LoginDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserResponseDTO;
import co.ucentral.gestor.tareas.metodologias.mappers.UserMapper;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateProfile(UserDTO userDTO, String username) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }


        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setPassword(userDTO.getPassword());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponseDTO(updatedUser);
    }

    public List<UserResponseDTO> getAssignedUsers(List<Long> assignedUserIds) {
        if (assignedUserIds == null || assignedUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        return assignedUserIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId)))
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll(); // Recupera todos los usuarios desde la base de datos
        return users.stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getFullName(), user.getPhoneNumber(), user.isAdmin()))
                .collect(Collectors.toList());
    }

    public UserResponseDTO loginUser(LoginDTO loginDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(loginDTO.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(loginDTO.getPassword())) {
                return new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getFullName(), user.getPhoneNumber(), user.isAdmin());
            }
        }
        throw new RuntimeException("Credenciales incorrectas");
    }



}
