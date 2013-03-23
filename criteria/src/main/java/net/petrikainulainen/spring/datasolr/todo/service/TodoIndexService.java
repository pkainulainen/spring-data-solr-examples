package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public interface TodoIndexService {

    public void addToIndex(Todo todoEntry);

    public void deleteFromIndex(Long id);

    public List<TodoDocument> search(String searchTerm);

    public void update(Todo todoEntry);
}
