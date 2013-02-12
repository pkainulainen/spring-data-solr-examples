package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import net.petrikainulainen.spring.datasolr.todo.repository.solr.TodoDocumentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Petri Kainulainen
 */
public class RepositoryTodoIndexServiceTest {

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

        assertEquals(todoEntry.getId(), todoDocument.getId());
        assertEquals(todoEntry.getDescription(), todoDocument.getDescription());
        assertEquals(todoEntry.getTitle(), todoDocument.getTitle());
    }

    @Test
    public void deleteFromIndex() {
        service.deleteFromIndex(1L);

        verify(repositoryMock, times(1)).delete(1L);
        verifyNoMoreInteractions(repositoryMock);
    }
}
