package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;

import java.util.List;

/**
 * @author Petri Kainulainen
 */
public interface TodoDocumentRepository extends CustomBaseRepository<TodoDocument, String> {

    public List<TodoDocument> findByTitleContainsOrDescriptionContains(String title, String description, Pageable page);

    @Query(name = "TodoDocument.findByNamedQuery")
    public List<TodoDocument> findByNamedQuery(String searchTerm, Pageable page);

    @Query("title:*?0* OR description:*?0*")
    public List<TodoDocument> findByQueryAnnotation(String searchTerm, Pageable page);
}
