package co.ucentral.gestor.tareas.metodologias.excepciones;

public class TaskException extends RuntimeException {
    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
}