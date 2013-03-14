package net.petrikainulainen.spring.datasolr.config;

import net.petrikainulainen.spring.datasolr.todo.repository.solr.TodoDocumentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * @author Petri Kainulainen
 */
@Configuration
@Profile("test")
public class TestSolrContext {

    @Bean
    public TodoDocumentRepository todoDocumentRepository() {
        return mock(TodoDocumentRepository.class);
    }
}
