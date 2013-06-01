package net.petrikainulainen.spring.datasolr.todo.repository.solr;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;

import java.io.Serializable;

/**
 * @author Petri Kainulainen
 */
public class CustomSolrRepositoryFactoryBean extends SolrRepositoryFactoryBean {

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new CustomSolrRepositoryFactory(getSolrOperations());
    }

    private static class CustomSolrRepositoryFactory<T, ID extends Serializable> extends SolrRepositoryFactory {

        private final SolrOperations solrOperations;

        public CustomSolrRepositoryFactory(SolrOperations solrOperations) {
            super(solrOperations);
            this.solrOperations = solrOperations;
        }

        @Override
        protected Object getTargetRepository(RepositoryMetadata metadata) {
            return new CustomBaseRepositoryImpl<T, ID>(solrOperations, (Class<T>) metadata.getDomainType());
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return CustomBaseRepository.class;
        }
    }
}
