package co.ucentral.gestor.tareas.metodologias.controladores;

import co.ucentral.gestor.tareas.metodologias.config.JwtUtil;
import co.ucentral.gestor.tareas.metodologias.dto.LoginDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserDTO;
import co.ucentral.gestor.tareas.metodologias.dto.UserResponseDTO;
import co.ucentral.gestor.tareas.metodologias.excepciones.ErrorResponse;
import co.ucentral.gestor.tareas.metodologias.servicios.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.registerUser(userDTO));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Valid @RequestBody UserDTO userDTO,
            Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(userDTO, principal.getName()));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<UserResponseDTO>> getAssignedUsers(
            @RequestParam List<Long> userIds) {
        return ResponseEntity.ok(userService.getAssignedUsers(userIds));
    }

    @GetMapping("/")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {

            String attemptMessage = "Intento de login para usuario: " + loginDTO.getUsername();
            UserResponseDTO user = userService.loginUser(loginDTO);
            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            response.put("message", "Login exitoso para usuario: " + loginDTO.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error en login para usuario: " + loginDTO.getUsername());
            errorResponse.put("message", "Credenciales inválidas: ," + e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        UserResponseDTO userResponse = userService.getProfile(principal.getName());
        return ResponseEntity.ok(userResponse);
    }


}