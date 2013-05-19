package net.petrikainulainen.spring.datasolr.todo.service;

import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.document.TodoDocument;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import net.petrikainulainen.spring.datasolr.todo.repository.solr.TodoDocumentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
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
    public void addToIndex_ShouldCreateNewDocument_ToIndex() {
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
    public void deleteFromIndex_ShouldDeleteDocumentFromIndex() {
        service.deleteFromIndex(1L);

        verify(repositoryMock, times(1)).delete("1");
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void search_ShouldReturnDocuments() {
        List<TodoDocument> expected = new ArrayList<TodoDocument>();
        when(repositoryMock.search(eq(SEARCH_TERM), any(Pageable.class))).thenReturn(expected);

        Pageable page = new PageRequest(0, 10);
        List<TodoDocument> actual = service.search(SEARCH_TERM, page);

        verify(repositoryMock, times(1)).search(SEARCH_TERM, page);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(expected, actual);
    }

    @Test
    public void update_ExistingTodo_ShouldUpdateDocument() {
        Todo todoEntry = TodoTestUtil.createModel(TodoTestUtil.ID, TodoTestUtil.DESCRIPTION, TodoTestUtil.TITLE);

        service.update(todoEntry);

        verify(repositoryMock, times(1)).update(todoEntry);
        verifyNoMoreInteractions(repositoryMock);
    }
}
