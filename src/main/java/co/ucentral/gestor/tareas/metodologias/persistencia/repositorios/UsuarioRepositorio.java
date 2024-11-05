package co.ucentral.gestor.tareas.metodologias.persistencia.repositorios;

import co.ucentral.gestor.tareas.metodologias.persistencia.entidades.Usuario;
import org.springframework.data.repository.CrudRepository;
public interface UsuarioRepositorio extends CrudRepository<Usuario, String> {

}
