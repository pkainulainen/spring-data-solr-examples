package net.petrikainulainen.spring.datasolr.todo.document;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;

/**
 * @author Petri Kainulainen
 */
public class TodoDocument {

    @Field("id")
    private Long id;

    @Field("description")
    private String description;

    @Field("title")
    private String title;

    public TodoDocument() {

    }

    public static Builder getBuilder(Long id, String title) {
        return new Builder(id, title);
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    public static class Builder {
        private TodoDocument build;

        public Builder(Long id, String title) {
            build = new TodoDocument();
            build.id = id;
            build.title = title;
        }

        public Builder description(String description) {
            build.description = description;
            return this;
        }

        public TodoDocument build() {
            return build;
        }
    }
}
