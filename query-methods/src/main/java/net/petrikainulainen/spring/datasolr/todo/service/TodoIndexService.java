package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.model.Todo;

/**
 * @author Petri Kainulainen
 */
public interface TodoIndexService {

    public void addToIndex(Todo todoEntry);

    public void deleteFromIndex(Long id);
}
