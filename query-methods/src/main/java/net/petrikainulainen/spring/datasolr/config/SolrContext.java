package net.petrikainulainen.spring.datasolr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactoryBean;
import org.springframework.data.solr.server.support.HttpSolrServerFactoryBean;

import javax.annotation.Resource;

/**
 * @author Petri Kainulainen
 */
@Configuration
@EnableSolrRepositories("net.petrikainulainen.spring.datasolr.todo.repository.solr")
public class SolrContext {

    private static final String PROPERTY_NAME_SOLR_SOLR_HOME = "solr.solr.home";
    private static final String PROPERTY_NAME_SOLR_SERVER_URL = "datasolr.server.url";

    @Resource
    private Environment environment;

    public EmbeddedSolrServerFactoryBean solrServerFactoryBean() {
        EmbeddedSolrServerFactoryBean factory = new EmbeddedSolrServerFactoryBean();

        factory.setSolrHome(environment.getRequiredProperty(PROPERTY_NAME_SOLR_SOLR_HOME));

        return factory;
    }

    /*
    @Bean
    public HttpSolrServerFactoryBean solrServerFactoryBean() {
        HttpSolrServerFactoryBean factory = new HttpSolrServerFactoryBean();

        factory.setUrl(environment.getRequiredProperty(PROPERTY_NAME_SOLR_SERVER_URL));

        return factory;
    }*/

    @Bean
    public SolrTemplate solrTemplate() throws Exception {
        return new SolrTemplate(solrServerFactoryBean().getObject());
    }
}
