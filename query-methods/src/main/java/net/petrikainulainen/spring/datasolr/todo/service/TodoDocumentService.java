package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.model.Todo;

/**
 * @author Petri Kainulainen
 */
public interface TodoDocumentService {

    public void deleteById(Long id);

    public void save(Todo todoEntry);
}
