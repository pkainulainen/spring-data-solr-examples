package net.petrikainulainen.spring.datasolr.todo.document;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Petri Kainulainen
 */
public class TodoDocumentTest {

    private static final Long ID = Long.valueOf(1);
    private static final String ID_AS_STRING = ID.toString();
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";

    @Test
    public void build_IdAndTitleAreGiven_ShouldBuildObject() {
        TodoDocument document = TodoDocument.getBuilder(ID, TITLE).build();

        assertEquals(ID_AS_STRING, document.getId());
        assertNull(document.getDescription());
        assertEquals(TITLE, document.getTitle());
    }

    @Test
    public void build_AllValuesAreGiven_ShouldBuildObject() {
        TodoDocument document = TodoDocument.getBuilder(ID, TITLE)
                .description(DESCRIPTION)
                .build();

        assertEquals(ID_AS_STRING, document.getId());
        assertEquals(DESCRIPTION, document.getDescription());
        assertEquals(TITLE, document.getTitle());
    }
}
