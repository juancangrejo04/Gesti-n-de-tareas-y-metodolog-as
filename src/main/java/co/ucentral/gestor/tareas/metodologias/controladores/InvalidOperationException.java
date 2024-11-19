package co.ucentral.gestor.tareas.metodologias.controladores;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}