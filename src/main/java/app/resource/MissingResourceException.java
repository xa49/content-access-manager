package app.resource;

public class MissingResourceException extends RuntimeException{
    public MissingResourceException(String message) {
        super(message);
    }
}
