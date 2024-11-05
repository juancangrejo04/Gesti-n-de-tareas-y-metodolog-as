package co.ucentral.gestor.tareas.metodologias.controladores;
import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Usuario;
import co.ucentral.gestor.tareas.metodologias.servicios.UsuarioServicios;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;
@AllArgsConstructor
@Controller
public class UsuarioControlador {

    UsuarioServicios usuarioServicio;

    @GetMapping("/registro")
    public String mostrarFormularioDeRegistro(Model model) {
        Usuario usuario = new Usuario();

        model.addAttribute("elusuario", usuario);
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(Usuario usuario, Model model) {
        usuarioServicio.registrarUsuario(usuario);
        model.addAttribute("mensaje", "Usuario registrado exitosamente");
        return "iniciosesion";


    }

    public List<Usuario> obtenerTodos() {
        return usuarioServicio.obtenerTodos();
    }
}
