package dev.estebannieva.categories.controller;

import dev.estebannieva.categories.client.TaskRestApiClient;
import dev.estebannieva.categories.model.Category;
import dev.estebannieva.categories.repository.CategoryRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class CategoryController {

  private final CategoryRepository repository;

  private final TaskRestApiClient taskClient;

  public CategoryController(CategoryRepository repository, TaskRestApiClient taskClient) {
    this.repository = repository;
    this.taskClient = taskClient;
  }

  @GetMapping("/categories")
  @ResponseStatus(HttpStatus.OK)
  public List<Category> listCategories() {
    List<Category> categories = repository.findAll();

    for (Category category : categories) {
      long taskCount = taskClient.countByCategoryId(category.getId());
      category.setTaskCount(taskCount);
    }

    return categories;
  }
}
