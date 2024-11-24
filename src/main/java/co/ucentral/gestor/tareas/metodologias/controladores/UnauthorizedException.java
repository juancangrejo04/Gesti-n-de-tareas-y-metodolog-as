package co.ucentral.gestor.tareas.metodologias.controladores;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}