package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.io.Serializable;

/**
 * @author Petri Kainulainen
 */
@NoRepositoryBean
public interface CustomBaseRepository<T, ID extends Serializable> extends SolrCrudRepository<T, ID> {

    public long count(String searchTerm);

    public void update(Todo todoEntry);
}
