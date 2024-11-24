package co.ucentral.gestor.tareas.metodologias.servicios;

import co.ucentral.gestor.tareas.metodologias.dto.LoginDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserResponseDTO;
import co.ucentral.gestor.tareas.metodologias.mappers.UserMapper;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.User;
import co.ucentral.gestor.tareas.metodologias.persistencia.repositorios.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO registerUser(UserDTO userDTO) {
        logger.info("Intentando registrar nuevo usuario: {}", userDTO.getUsername());

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAdmin(userDTO.isAdmin());

        User savedUser = userRepository.save(user);
        logger.info("Usuario registrado exitosamente: {}", userDTO.getUsername());
        logger.info("Contraseña original: {}", userDTO.getPassword());
        logger.info("Contraseña encodeada: {}", encodedPassword);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getPhoneNumber(),
                savedUser.isAdmin()
        );
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

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            existingUser.setPassword(encodedPassword);
        }

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
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserResponseDTO(user.getId(), user.getEmail(), user.getUsername(), user.getFullName(), user.getPhoneNumber(), user.isAdmin()))
                .collect(Collectors.toList());
    }

    public UserResponseDTO loginUser(LoginDTO loginDTO) {
        logger.info("Intentando login para usuario: {}", loginDTO.getUsername());
        Optional<User> optionalUser = userRepository.findByUsername(loginDTO.getUsername());
        if (!optionalUser.isPresent()) {
            logger.warn("Usuario no encontrado: {}", loginDTO.getUsername());
            throw new RuntimeException("Usuario no encontrado");
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.warn("Contraseña incorrecta para usuario: {}", loginDTO.getUsername());
            throw new RuntimeException("Contraseña incorrecta");
        }

        logger.info("Login exitoso para usuario: {}", loginDTO.getUsername());

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.isAdmin()
        );
    }



}
