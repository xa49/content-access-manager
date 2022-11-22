package app.resource;

public class UserErrorException extends RuntimeException{
    public UserErrorException(String message) {
        super(message);
    }
}
