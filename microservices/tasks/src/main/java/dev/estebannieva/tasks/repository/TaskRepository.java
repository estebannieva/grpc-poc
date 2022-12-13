package dev.estebannieva.tasks.repository;

import dev.estebannieva.tasks.model.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

  boolean existsByCategoryId(String categoryId);
  long countByCategoryId(String categoryId);

  List<Task> findAllByCategoryId(String categoryId);

  List<Task> findAllByCategoryId(String categoryId, Pageable pageable);
}
