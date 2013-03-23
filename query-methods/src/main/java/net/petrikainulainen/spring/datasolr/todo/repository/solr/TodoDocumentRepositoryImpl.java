package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author Petri Kainulainen
 */
@Repository
public class TodoDocumentRepositoryImpl implements PartialUpdateRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoDocumentRepositoryImpl.class);

    @Resource
    private SolrTemplate solrTemplate;

    @Override
    public void update(Todo todoEntry) {
        LOGGER.debug("Performing partial update for todo entry: {}", todoEntry);

        PartialUpdate update = new PartialUpdate(TodoDocument.FIELD_ID, todoEntry.getId().toString());

        update.add(TodoDocument.FIELD_DESCRIPTION, todoEntry.getDescription());
        update.add(TodoDocument.FIELD_TITLE, todoEntry.getTitle());

        solrTemplate.saveBean(update);
        solrTemplate.commit();
    }
}
