package net.petrikainulainen.spring.datasolr.todo.dto;

/**
 * @author Petri Kainulainen
 */
public class FieldValidationErrorDTO {

    private String path;
    private String message;

    public FieldValidationErrorDTO(String path, String message) {
        this.path = path;
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }
}
