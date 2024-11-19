package co.ucentral.gestor.tareas.metodologias.controladores;

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
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

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
            UserResponseDTO userResponse = userService.loginUser(loginDTO);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Credenciales incorrectas"));
        }
    }


}