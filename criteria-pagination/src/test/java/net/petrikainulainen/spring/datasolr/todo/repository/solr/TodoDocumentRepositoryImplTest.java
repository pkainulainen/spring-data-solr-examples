package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class TodoDocumentRepositoryImplTest {

    private static final long RESULT_COUNT = 2;

    private static final String SEARCH_TERM_TWO_WORDS = "foo bar";
    private static final String SEARCH_TERM_SINGLE_WORD = "foo";

    private static final String WORD_ONE = "foo";
    private static final String WORD_TWO = "bar";

    private TodoDocumentRepositoryImpl repository;

    private SolrTemplate solrTemplateMock;

    @Before
    public void setUp() {
        repository = new TodoDocumentRepositoryImpl();

        solrTemplateMock = mock(SolrTemplate.class);
        ReflectionTestUtils.setField(repository, "solrTemplate", solrTemplateMock);
    }

    @Test
    public void count_SingleWord_ShouldCreateConditionAndReturnSearchResultCount() {
        when(solrTemplateMock.count(any(Query.class))).thenReturn(RESULT_COUNT);

        long actual = repository.count(SEARCH_TERM_SINGLE_WORD);

        ArgumentCaptor<SimpleQuery> queryArgument = ArgumentCaptor.forClass(SimpleQuery.class);
        verify(solrTemplateMock, times(1)).count(queryArgument.capture());
        verifyNoMoreInteractions(solrTemplateMock);

        SimpleQuery executedQuery = queryArgument.getValue();
        Criteria executedCriteria = executedQuery.getCriteria();

        List<Criteria> criteriaChain = executedCriteria.getCriteriaChain();
        assertEquals(2, criteriaChain.size());

        for (Criteria criteria: criteriaChain) {
            Field field = criteria.getField();
            Assert.assertTrue(field.getName().equals(TodoDocument.FIELD_TITLE) || field.getName().equals(TodoDocument.FIELD_DESCRIPTION));

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
        when(solrTemplateMock.count(any(Query.class))).thenReturn(RESULT_COUNT);

        long actual = repository.count(SEARCH_TERM_TWO_WORDS);

        ArgumentCaptor<SimpleQuery> queryArgument = ArgumentCaptor.forClass(SimpleQuery.class);
        verify(solrTemplateMock, times(1)).count(queryArgument.capture());
        verifyNoMoreInteractions(solrTemplateMock);

        SimpleQuery executedQuery = queryArgument.getValue();
        Criteria executedCriteria = executedQuery.getCriteria();

        List<Criteria> criteriaChain = executedCriteria.getCriteriaChain();
        assertEquals(4, criteriaChain.size());

        for (Criteria criteria: criteriaChain) {
            Field field = criteria.getField();
            Assert.assertTrue(field.getName().equals(TodoDocument.FIELD_TITLE) || field.getName().equals(TodoDocument.FIELD_DESCRIPTION));

            Set<Criteria.CriteriaEntry> entries = criteria.getCriteriaEntries();
            assertEquals(1, entries.size());

            Criteria.CriteriaEntry entry = entries.iterator().next();
            assertEquals(Criteria.OperationKey.CONTAINS.getKey(), entry.getKey());
            Assert.assertTrue(WORD_ONE.equals(entry.getValue()) || WORD_TWO.equals(entry.getValue()));
        }

        assertEquals(RESULT_COUNT, actual);
    }

    @Test
    public void search_SingleWord_ShouldCreateConditionAndReturnDocuments() {
        List<TodoDocument> expected = new ArrayList<TodoDocument>();
        Page expectedPage = new PageImpl(expected);
        when(solrTemplateMock.queryForPage(any(Query.class), eq(TodoDocument.class))).thenReturn(expectedPage);

        Pageable pageSpec = new PageRequest(0, 10);
        List<TodoDocument> actual = repository.search(SEARCH_TERM_SINGLE_WORD, pageSpec);

        ArgumentCaptor<SimpleQuery> queryArgument = ArgumentCaptor.forClass(SimpleQuery.class);
        verify(solrTemplateMock, times(1)).queryForPage(queryArgument.capture(), eq(TodoDocument.class));
        verifyNoMoreInteractions(solrTemplateMock);

        SimpleQuery executedQuery = queryArgument.getValue();

        Pageable actualPageSpec = executedQuery.getPageRequest();
        assertEquals(pageSpec, actualPageSpec);

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

        assertEquals(expected, actual);
    }

    @Test
    public void search_MultipleWords_ShouldCreateConditionAndReturnDocuments() {
        List<TodoDocument> expected = new ArrayList<TodoDocument>();
        Page expectedPage = new PageImpl(expected);
        when(solrTemplateMock.queryForPage(any(Query.class), eq(TodoDocument.class))).thenReturn(expectedPage);

        Pageable pageSpec = new PageRequest(0, 10);
        List<TodoDocument> actual = repository.search(SEARCH_TERM_TWO_WORDS, pageSpec);

        ArgumentCaptor<SimpleQuery> queryArgument = ArgumentCaptor.forClass(SimpleQuery.class);
        verify(solrTemplateMock, times(1)).queryForPage(queryArgument.capture(), eq(TodoDocument.class));
        verifyNoMoreInteractions(solrTemplateMock);

        SimpleQuery executedQuery = queryArgument.getValue();
        Criteria executedCriteria = executedQuery.getCriteria();

        Pageable actualPageSpec = executedQuery.getPageRequest();
        assertEquals(pageSpec, actualPageSpec);

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

        assertEquals(expected, actual);
    }

    @Test
    public void update_ExistingDocument_ShouldDoPartialUpdate() {
        Todo todoEntry = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        repository.update(todoEntry);

        ArgumentCaptor<PartialUpdate> partialUpdateArgument = ArgumentCaptor.forClass(PartialUpdate.class);
        verify(solrTemplateMock, times(1)).saveBean(partialUpdateArgument.capture());

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
