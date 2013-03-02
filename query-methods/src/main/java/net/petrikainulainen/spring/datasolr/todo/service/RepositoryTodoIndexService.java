package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import net.petrikainulainen.spring.datasolr.todo.repository.solr.TodoDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Petri Kainulainen
 */
@Service
public class RepositoryTodoIndexService implements TodoIndexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryTodoIndexService.class);

    protected static final String QUERY_METHOD_METHOD_NAME = "methodName";
    protected static final String QUERY_METHOD_NAMED_QUERY = "namedQuery";
    protected static final String QUERY_METHOD_QUERY_ANNOTATION = "queryAnnotation";

    @Resource
    private TodoDocumentRepository repository;

    @Value("${solr.repository.query.method.type}")
    private String queryMethodType;

    @Transactional
    @Override
    public void addToIndex(Todo todoEntry) {
        LOGGER.debug("Saving a todo entry with information: {}", todoEntry);
        TodoDocument document = TodoDocument.getBuilder(todoEntry.getId(), todoEntry.getTitle())
                .description(todoEntry.getDescription())
                .build();

        LOGGER.debug("Saving document with information: {}", document);

        repository.save(document);
    }

    @Transactional
    @Override
    public void deleteFromIndex(Long id) {
        LOGGER.debug("Deleting an existing document with id: {}", id);
        repository.delete(id.toString());
    }

    @Transactional
    @Override
    public List<TodoDocument> search(String searchTerm) {
        LOGGER.debug("Searching documents with search term: {}", searchTerm);
        return findDocuments(searchTerm);
    }

    private List<TodoDocument> findDocuments(String searchTerm) {
        if (queryMethodType != null) {
            if (queryMethodType.equals(QUERY_METHOD_METHOD_NAME)) {
                LOGGER.debug("Finding todo entries by using query generation from method name.");
                return repository.findByTitleContainsOrDescriptionContains(searchTerm, searchTerm);
            }
            else if (queryMethodType.equals(QUERY_METHOD_NAMED_QUERY)) {
                LOGGER.debug("Finding todo entries by using named queries.");
                return repository.findByNamedQuery(searchTerm);
            }
            else if (queryMethodType.equals(QUERY_METHOD_QUERY_ANNOTATION)) {
                LOGGER.debug("Finding todo entries by using @Query annotation.");
                return repository.findByQueryAnnotation(searchTerm);
            }
        }

        LOGGER.debug("Unknown query method type. Returning empty list.");
        return new ArrayList<TodoDocument>();
    }
}
