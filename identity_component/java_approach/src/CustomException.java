package identity_component.java_approach.src;

public class CustomException extends Exception {
    public CustomException(String message) {
        super(message);
        printStackTrace();
    }
}