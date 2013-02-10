package net.petrikainulainen.spring.datasolr.todo.repository;

import net.petrikainulainen.spring.datasolr.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Petri Kainulainen
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {
}
