package co.ucentral.gestor.tareas.metodologias.excepciones;

public class TaskException extends RuntimeException {

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class TaskNotFoundException extends TaskException {
        public TaskNotFoundException(String message) {
            super(message);
        }

        public TaskNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UnauthorizedAccessException extends TaskException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
        public UnauthorizedAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class IllegalTaskStateException extends TaskException {
        public IllegalTaskStateException(String message) {
            super(message);
        }
        public IllegalTaskStateException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
