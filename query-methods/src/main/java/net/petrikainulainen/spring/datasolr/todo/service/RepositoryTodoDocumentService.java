package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import net.petrikainulainen.spring.datasolr.todo.repository.solr.TodoDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Petri Kainulainen
 */
@Service
public class RepositoryTodoDocumentService implements TodoDocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryTodoDocumentService.class);

    @Resource
    private TodoDocumentRepository repository;

    @Override
    public void add(Todo todoEntry) {
        LOGGER.debug("Adding a new todo document with information: {}", todoEntry);
        saveDocument(todoEntry);
    }

    @Override
    public void deleteById(Long id) {
        LOGGER.debug("Deleting an existing document with id: {}", id);
        repository.delete(id);
    }

    @Override
    public void update(Todo todoEntry) {
        LOGGER.debug("Updating an existing document with information: {}", todoEntry);
        saveDocument(todoEntry);
    }

    private void saveDocument(Todo todoEntry) {
        TodoDocument document = TodoDocument.getBuilder(todoEntry.getId(), todoEntry.getTitle())
                .description(todoEntry.getDescription())
                .build();

        LOGGER.debug("Saving document with information: {}", document);

        repository.save(document);
    }
}
