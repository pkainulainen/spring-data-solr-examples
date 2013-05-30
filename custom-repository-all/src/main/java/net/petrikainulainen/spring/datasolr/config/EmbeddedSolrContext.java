package net.petrikainulainen.spring.datasolr.config;

import net.petrikainulainen.spring.datasolr.todo.repository.solr.CustomSolrRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactoryBean;

import javax.annotation.Resource;

/**
 * @author Petri Kainulainen
 */
@Configuration
@EnableSolrRepositories(
        basePackages = "net.petrikainulainen.spring.datasolr.todo.repository.solr",
        repositoryFactoryBeanClass = CustomSolrRepositoryFactoryBean.class
)
@Profile("dev")
public class EmbeddedSolrContext {

    private static final String PROPERTY_NAME_SOLR_SOLR_HOME = "solr.solr.home";

    @Resource
    private Environment environment;

    @Bean
    public EmbeddedSolrServerFactoryBean solrServerFactoryBean() {
        EmbeddedSolrServerFactoryBean factory = new EmbeddedSolrServerFactoryBean();

        factory.setSolrHome(environment.getRequiredProperty(PROPERTY_NAME_SOLR_SOLR_HOME));

        return factory;
    }

    @Bean
    public SolrTemplate solrTemplate() throws Exception {
        return new SolrTemplate(solrServerFactoryBean().getObject());
    }
}
