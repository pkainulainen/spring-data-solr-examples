package net.petrikainulainen.spring.datasolr.todo.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import net.petrikainulainen.spring.datasolr.config.ExampleApplicationContext;
import net.petrikainulainen.spring.datasolr.config.TestSolrContext;
import net.petrikainulainen.spring.datasolr.todo.TodoTestUtil;
import net.petrikainulainen.spring.datasolr.todo.dto.TodoDTO;
import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.server.samples.context.SecurityRequestPostProcessors.userDetailsService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test uses the annotation based application context configuration.
 * @author Petri Kainulainen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExampleApplicationContext.class, TestSolrContext.class})
//@ContextConfiguration(locations = {"classpath:exampleApplicationContext.xml", "classpath:exampleApplicationContext-test.xml"})
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("toDoData.xml")
public class ITTodoControllerTest {

    @Resource
    private FilterChainProxy springSecurityFilterChain;

    @Resource
    private WebApplicationContext webApplicationContext;

    @Resource
    private DataSource datasource;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addAsAnonymous_AllFieldsOk_ShouldReturnStatusUnauthorized() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "description", "title");
        mockMvc.perform(post("/api/todo")
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase(value="toDoData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void addAsUser_AllFieldsOk_ShouldReturnAddedTodo() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "description", "title");
        mockMvc.perform(post("/api/todo")
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(added))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":3,\"description\":\"description\",\"title\":\"title\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addAsAnonymous_EmptyTodo_ShouldReturnStatusBadRequest() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "", "");
        mockMvc.perform(post("/api/todo")
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addAsUser_EmptyTodo_ShouldReturnFormValidationErrors() throws Exception {
        TodoDTO added = TodoTestUtil.createDTO(null, "", "");
        mockMvc.perform(post("/api/todo")
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(added))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"fieldErrors\":[{\"path\":\"title\",\"message\":\"The title cannot be empty.\"}]}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addAsAnonymous_TitleAndDescriptionAreTooLong_ShouldReturnStatusBadRequest() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        TodoDTO added = TodoTestUtil.createDTO(null, description, title);

        mockMvc.perform(post("/api/todo")
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(added))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void addAsUser_TitleAndDescriptionAreTooLong_ShouldReturnFormValidationErrors() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);
        TodoDTO added = TodoTestUtil.createDTO(null, description, title);

        mockMvc.perform(post("/api/todo")
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(added))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(startsWith("{\"fieldErrors\":[")))
                .andExpect(content().string(allOf(
                        containsString("{\"path\":\"description\",\"message\":\"The maximum length of the description is 500 characters.\"}"),
                        containsString("{\"path\":\"title\",\"message\":\"The maximum length of the title is 100 characters.\"}")
                )))
                .andExpect(content().string(endsWith("]}")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdAsAnonymous_TodoFound_ShouldReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData-delete-expected.xml")
    public void deleteByIdAsUser_TodoFound_ShouldReturnDeletedUser() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 1L)
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":1,\"description\":\"Lorem ipsum\",\"title\":\"Foo\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdAsAnonymous_TodoIsNotFound_ShouldReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 3L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void deleteByIdAsUser_TodoIsNotFound_ShouldReturnStatusNotFound() throws Exception {
        mockMvc.perform(delete("/api/todo/{id}", 3L)
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findAllAsAnonymous_ShouldReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findAllAsUser_ShouldReturnTodoList() throws Exception {
        mockMvc.perform(get("/api/todo")
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[{\"id\":1,\"description\":\"Lorem ipsum\",\"title\":\"Foo\"},{\"id\":2,\"description\":\"Lorem ipsum\",\"title\":\"Bar\"}]"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdAsAnonymous_TodoFound_ShouldReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdAsUser_TodoFound_ShouldReturnTodo() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 1L)
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":1,\"description\":\"Lorem ipsum\",\"title\":\"Foo\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdAsAnonymous_TodoIsNotFound_ShouldReturnStatusUnauthorized() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 3L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void findByIdAsUser_TodoIsNotFound_ShouldReturnStatusNotFound() throws Exception {
        mockMvc.perform(get("/api/todo/{id}", 3L)
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isNotFound());
    }

    @Test
    @ExpectedDatabase(value="toDoData.xml")
    public void updateAsAnonymous_TodoFound_ShouldReturnStatusUnauthorized() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase(value="toDoData-update-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void updateAsUser_TodoFound_ShouldReturnUpdatedTodo() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"id\":1,\"description\":\"description\",\"title\":\"title\"}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateAsAnonymous_TodoEmpty_ShouldReturnStatusBadRequest() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "", "");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateUser_EmptyTodo_ShouldReturnFormValidationErrors() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(1L, "", "");

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"fieldErrors\":[{\"path\":\"title\",\"message\":\"The title cannot be empty.\"}]}"));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateAsAnonymous_TitleAndDescriptionAreTooLong_ShouldReturnStatusBadRequest() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO updated = TodoTestUtil.createDTO(1L, description, title);

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateAsUser_TitleAndDescriptionAreTooLong_ShouldReturnFormValidationErrors() throws Exception {
        String title = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_TITLE + 1);
        String description = TodoTestUtil.createStringWithLength(Todo.MAX_LENGTH_DESCRIPTION + 1);

        TodoDTO updated = TodoTestUtil.createDTO(1L, description, title);

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string(startsWith("{\"fieldErrors\":[")))
                .andExpect(content().string(allOf(
                        containsString("{\"path\":\"description\",\"message\":\"The maximum length of the description is 500 characters.\"}"),
                        containsString("{\"path\":\"title\",\"message\":\"The maximum length of the title is 100 characters.\"}")
                )))
                .andExpect(content().string(endsWith("]}")));
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateAsAnonymous_TodoIsNotFound_ShouldReturnStatusUnauthorized() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(3L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 3L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @ExpectedDatabase("toDoData.xml")
    public void updateAsUser_TodoIsNotFound_ShouldReturnStatusNotFound() throws Exception {
        TodoDTO updated = TodoTestUtil.createDTO(3L, "description", "title");

        mockMvc.perform(put("/api/todo/{id}", 3L)
                .contentType(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.APPLICATION_JSON_UTF8)
                .content(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.convertObjectToJsonBytes(updated))
                .with(userDetailsService(net.petrikainulainen.spring.datasolr.IntegrationTestUtil.CORRECT_USERNAME))
        )
                .andExpect(status().isNotFound());
    }
}

