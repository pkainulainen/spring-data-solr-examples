package net.petrikainulainen.spring.datasolr.todo.exception;

/**
 * @author Petri Kainulainen
 */
public class TodoNotFoundException extends Exception {

    public TodoNotFoundException(String message) {
        super(message);
    }

}
