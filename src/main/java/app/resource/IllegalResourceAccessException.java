package app.resource;

public class IllegalResourceAccessException extends RuntimeException{
    public IllegalResourceAccessException(String message) {
        super(message);
    }
}
