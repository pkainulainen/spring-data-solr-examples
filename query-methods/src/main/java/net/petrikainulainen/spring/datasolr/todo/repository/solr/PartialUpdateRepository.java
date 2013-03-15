package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;

/**
 * @author Petri Kainulainen
 */
public interface PartialUpdateRepository {

    public void update(Todo todoEntry);
}
