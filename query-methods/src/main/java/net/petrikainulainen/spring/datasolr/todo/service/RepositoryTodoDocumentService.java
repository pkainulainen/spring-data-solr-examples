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
    public void deleteById(Long id) {
        LOGGER.debug("Deleting an existing document with id: {}", id);
        repository.delete(id);
    }

    @Override
    public void save(Todo todoEntry) {
        LOGGER.debug("Saving a todo entry with information: {}", todoEntry);
        TodoDocument document = TodoDocument.getBuilder(todoEntry.getId(), todoEntry.getTitle())
                .description(todoEntry.getDescription())
                .build();

        LOGGER.debug("Saving document with information: {}", document);

        repository.save(document);
    }
}
