package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public interface TodoDocumentRepository extends SolrCrudRepository<TodoDocument, String> {

    public List<TodoDocument> findByTitleContainsOrDescriptionContains(String title, String description);

    @Query(name = "TodoDocument.findByNamedQuery")
    public List<TodoDocument> findByNamedQuery(String searchTerm);

    @Query("title:*?0* OR description:*?0*")
    public List<TodoDocument> findByQueryAnnotation(String searchTerm);
}
