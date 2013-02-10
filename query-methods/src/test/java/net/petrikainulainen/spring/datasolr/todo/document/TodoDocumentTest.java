package net.petrikainulainen.spring.datasolr.todo.document;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author Petri Kainulainen
 */
public class TodoDocumentTest {

    private static final Long ID = Long.valueOf(1);
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";

    @Test
    public void buildWithMandatoryValues() {
        TodoDocument document = TodoDocument.getBuilder(ID, TITLE).build();

        assertEquals(ID, document.getId());
        assertNull(document.getDescription());
        assertEquals(TITLE, document.getTitle());
    }

    @Test
    public void buildWithAllValues() {
        TodoDocument document = TodoDocument.getBuilder(ID, TITLE)
                .description(DESCRIPTION)
                .build();

        assertEquals(ID, document.getId());
        assertEquals(DESCRIPTION, document.getDescription());
        assertEquals(TITLE, document.getTitle());
    }
}
