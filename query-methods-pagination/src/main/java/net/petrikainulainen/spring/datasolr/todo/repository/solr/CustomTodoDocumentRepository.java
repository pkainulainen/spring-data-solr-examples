package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.model.Todo;

/**
 * @author Petri Kainulainen
 */
public interface CustomTodoDocumentRepository {

    public long count(String searchTerm);

    public void update(Todo todoEntry);
}
