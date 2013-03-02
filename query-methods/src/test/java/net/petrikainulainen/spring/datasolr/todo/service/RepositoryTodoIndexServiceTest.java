package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import net.petrikainulainen.spring.datasolr.todo.repository.solr.TodoDocumentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RepositoryTodoIndexServiceTest {

    private static final String SEARCH_TERM = "Foo";

    private RepositoryTodoIndexService service;

    private TodoDocumentRepository repositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryTodoIndexService();

        repositoryMock = mock(TodoDocumentRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
    }

    @Test
    public void addToIndex() {
        Todo todoEntry = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        service.addToIndex(todoEntry);

        ArgumentCaptor<TodoDocument> todoDocumentArgument = ArgumentCaptor.forClass(TodoDocument.class);
        verify(repositoryMock, times(1)).save(todoDocumentArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        TodoDocument todoDocument = todoDocumentArgument.getValue();

        assertEquals(todoEntry.getId().toString(), todoDocument.getId());
        assertEquals(todoEntry.getDescription(), todoDocument.getDescription());
        assertEquals(todoEntry.getTitle(), todoDocument.getTitle());
    }

    @Test
    public void deleteFromIndex() {
        service.deleteFromIndex(1L);

        verify(repositoryMock, times(1)).delete("1");
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void searchWhenQueryGenerationFromMethodNameIsUsed() {
        ReflectionTestUtils.setField(service, "queryMethodType", RepositoryTodoIndexService.QUERY_METHOD_METHOD_NAME);

        service.search(SEARCH_TERM);

        verify(repositoryMock, times(1)).findByTitleContainsOrDescriptionContains(SEARCH_TERM, SEARCH_TERM);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void searchWhenNamedQueryIsUsed() {
        ReflectionTestUtils.setField(service, "queryMethodType", RepositoryTodoIndexService.QUERY_METHOD_NAMED_QUERY);

        service.search(SEARCH_TERM);

        verify(repositoryMock, times(1)).findByNamedQuery(SEARCH_TERM);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void searchWhenQueryAnnotationIsUsed() {
        ReflectionTestUtils.setField(service, "queryMethodType", RepositoryTodoIndexService.QUERY_METHOD_QUERY_ANNOTATION);

        service.search(SEARCH_TERM);

        verify(repositoryMock, times(1)).findByQueryAnnotation(SEARCH_TERM);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void searchWhenQueryMethodTypeIsUnknown() {
        ReflectionTestUtils.setField(service, "queryMethodType", "unknown");

        List<TodoDocument> todos = service.search(SEARCH_TERM);

        verifyZeroInteractions(repositoryMock);
        assertTrue(todos.isEmpty());
    }

    @Test
    public void searchWhenQueryMethodTypeIsNotSet() {
        List<TodoDocument> todos = service.search(SEARCH_TERM);

        verifyZeroInteractions(repositoryMock);
        assertTrue(todos.isEmpty());
    }
}
