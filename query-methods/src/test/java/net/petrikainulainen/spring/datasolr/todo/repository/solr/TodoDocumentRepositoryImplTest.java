package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.PartialUpdate;
import org.springframework.data.solr.core.query.UpdateField;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Petri Kainulainen
 */
public class TodoDocumentRepositoryImplTest {

    private TodoDocumentRepositoryImpl repository;

    private SolrTemplate solrTemplateMock;

    @Before
    public void setUp() {
        repository = new TodoDocumentRepositoryImpl();

        solrTemplateMock = mock(SolrTemplate.class);
        ReflectionTestUtils.setField(repository, "solrTemplate", solrTemplateMock);
    }

    @Test
    public void update_ExistingDocument_ShouldDoPartialUpdate() {
        Todo todoEntry = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        repository.update(todoEntry);

        ArgumentCaptor<PartialUpdate> partialUpdateArgument = ArgumentCaptor.forClass(PartialUpdate.class);
        verify(solrTemplateMock, times(1)).saveBean(partialUpdateArgument.capture());
        verify(solrTemplateMock, times(1)).commit();

        PartialUpdate update = partialUpdateArgument.getValue();

        assertEquals(todoEntry.getId().toString(), update.getIdField().getValue());

        List<UpdateField> updatedFields = update.getUpdates();
        assertEquals(2, updatedFields.size());

        for (UpdateField updatedField: updatedFields) {
            String fieldName = updatedField.getName();
            if (fieldName.equals(TodoDocument.FIELD_DESCRIPTION)) {
                assertEquals(todoEntry.getDescription(), updatedField.getValue());
            }
            else if (fieldName.equals(TodoDocument.FIELD_TITLE)) {
                assertEquals(todoEntry.getTitle(), updatedField.getValue());
            }
            else {
                fail("Unknown field: " + fieldName);
            }
        }
    }
}
