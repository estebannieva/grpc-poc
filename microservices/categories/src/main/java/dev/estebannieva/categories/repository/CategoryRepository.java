package dev.estebannieva.categories.repository;

import dev.estebannieva.categories.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

  boolean existsByName(String name);
}
