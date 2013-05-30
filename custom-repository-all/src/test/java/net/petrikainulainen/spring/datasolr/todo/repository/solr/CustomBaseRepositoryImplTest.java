package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.*;

import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class CustomBaseRepositoryImplTest {

    private static final long RESULT_COUNT = 2;

    private static final String SEARCH_TERM_TWO_WORDS = "foo bar";
    private static final String SEARCH_TERM_SINGLE_WORD = "foo";

    private static final String WORD_ONE = "foo";
    private static final String WORD_TWO = "bar";

    private CustomBaseRepositoryImpl repository;

    private SolrOperations solrOperationsMock;

    @Before
    public void setUp() {
        solrOperationsMock = mock(SolrOperations.class);
        repository = new CustomBaseRepositoryImpl(solrOperationsMock, TodoDocument.class);
    }


    @Test
    public void count_SingleWord_ShouldCreateConditionAndReturnSearchResultCount() {
        when(solrOperationsMock.count(any(Query.class))).thenReturn(RESULT_COUNT);

        long actual = repository.count(SEARCH_TERM_SINGLE_WORD);

        ArgumentCaptor<SimpleQuery> queryArgument = ArgumentCaptor.forClass(SimpleQuery.class);
        verify(solrOperationsMock, times(1)).count(queryArgument.capture());
        verifyNoMoreInteractions(solrOperationsMock);

        SimpleQuery executedQuery = queryArgument.getValue();
        Criteria executedCriteria = executedQuery.getCriteria();

        List<Criteria> criteriaChain = executedCriteria.getCriteriaChain();
        assertEquals(2, criteriaChain.size());

        for (Criteria criteria: criteriaChain) {
            Field field = criteria.getField();
            assertTrue(field.getName().equals(TodoDocument.FIELD_TITLE) || field.getName().equals(TodoDocument.FIELD_DESCRIPTION));

            Set<Criteria.CriteriaEntry> entries = criteria.getCriteriaEntries();
            assertEquals(1, entries.size());

            Criteria.CriteriaEntry entry = entries.iterator().next();
            assertEquals(Criteria.OperationKey.CONTAINS.getKey(), entry.getKey());
            assertEquals(SEARCH_TERM_SINGLE_WORD, entry.getValue());
        }

        assertEquals(RESULT_COUNT, actual);
    }

    @Test
    public void count_MultipleWords_ShouldCreateConditionAndReturnDocuments() {
        when(solrOperationsMock.count(any(Query.class))).thenReturn(RESULT_COUNT);

        long actual = repository.count(SEARCH_TERM_TWO_WORDS);

        ArgumentCaptor<SimpleQuery> queryArgument = ArgumentCaptor.forClass(SimpleQuery.class);
        verify(solrOperationsMock, times(1)).count(queryArgument.capture());
        verifyNoMoreInteractions(solrOperationsMock);

        SimpleQuery executedQuery = queryArgument.getValue();
        Criteria executedCriteria = executedQuery.getCriteria();

        List<Criteria> criteriaChain = executedCriteria.getCriteriaChain();
        assertEquals(4, criteriaChain.size());

        for (Criteria criteria: criteriaChain) {
            Field field = criteria.getField();
            assertTrue(field.getName().equals(TodoDocument.FIELD_TITLE) || field.getName().equals(TodoDocument.FIELD_DESCRIPTION));

            Set<Criteria.CriteriaEntry> entries = criteria.getCriteriaEntries();
            assertEquals(1, entries.size());

            Criteria.CriteriaEntry entry = entries.iterator().next();
            assertEquals(Criteria.OperationKey.CONTAINS.getKey(), entry.getKey());
            assertTrue(WORD_ONE.equals(entry.getValue()) || WORD_TWO.equals(entry.getValue()));
        }

        assertEquals(RESULT_COUNT, actual);
    }

    @Test
    public void update_ExistingDocument_ShouldDoPartialUpdate() {
        Todo todoEntry = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        repository.update(todoEntry);

        ArgumentCaptor<PartialUpdate> partialUpdateArgument = ArgumentCaptor.forClass(PartialUpdate.class);
        verify(solrOperationsMock, times(1)).saveBean(partialUpdateArgument.capture());
        verify(solrOperationsMock, times(1)).commit();

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
