package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.model.Todo;

/**
 * @author Petri Kainulainen
 */
public interface CustomTodoRepository {

    public long count(String searchTerm);

    public void update(Todo todoEntry);
}
