package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.support.SimpleSolrRepository;

import java.io.Serializable;

/**
 * @author Petri Kainulainen
 */
public class CustomBaseRepositoryImpl<T, ID extends Serializable> extends SimpleSolrRepository<T, ID> implements CustomBaseRepository<T, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomBaseRepositoryImpl.class);

    public CustomBaseRepositoryImpl(SolrOperations solrOperations, Class<T> entityClass) {
        super(solrOperations, entityClass);
    }

    @Override
    public long count(String searchTerm) {
        LOGGER.debug("Finding count for search term: {}", searchTerm);

        String[] words = searchTerm.split(" ");
        Criteria conditions = createSearchConditions(words);
        SimpleQuery countQuery = new SimpleQuery(conditions);

        return getSolrOperations().count(countQuery);
    }

    private Criteria createSearchConditions(String[] words) {
        Criteria conditions = null;

        for (String word: words) {
            if (conditions == null) {
                conditions = new Criteria(TodoDocument.FIELD_TITLE).contains(word)
                        .or(new Criteria(TodoDocument.FIELD_DESCRIPTION).contains(word));
            }
            else {
                conditions = conditions.or(new Criteria(TodoDocument.FIELD_TITLE).contains(word))
                        .or(new Criteria(TodoDocument.FIELD_DESCRIPTION).contains(word));
            }
        }

        return conditions;
    }

    @Override
    public void update(Todo todoEntry) {
        LOGGER.debug("Performing partial update for todo entry: {}", todoEntry);

        PartialUpdate update = new PartialUpdate(TodoDocument.FIELD_ID, todoEntry.getId().toString());

        update.add(TodoDocument.FIELD_DESCRIPTION, todoEntry.getDescription());
        update.add(TodoDocument.FIELD_TITLE, todoEntry.getTitle());

        getSolrOperations().saveBean(update);
        getSolrOperations().commit();
    }
}
