package dev.estebannieva.tasks.controller;

import dev.estebannieva.tasks.exception.NotFoundException;
import dev.estebannieva.tasks.mapper.TaskMapper;
import dev.estebannieva.tasks.model.Task;
import dev.estebannieva.tasks.repository.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class TaskController {

  private final TaskRepository repository;

  private final TaskMapper mapper;

  public TaskController(TaskRepository repository, TaskMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @PostMapping("/tasks")
  @ResponseStatus(HttpStatus.CREATED)
  public Task createTask(@RequestBody Task task) {
    return repository.save(task);
  }

  @PutMapping("/tasks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTask(@PathVariable String id, @RequestBody Task task) {
    Optional<Task> optionalTask = repository.findById(id);

    if (optionalTask.isEmpty()) {
      throw new NotFoundException("Task not found");
    }

    Task updateTask = mapper.mergeTask(id, task, optionalTask.get());

    repository.save(updateTask);
  }

  @DeleteMapping("/tasks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTask(@PathVariable String id) {
    boolean exists = repository.existsById(id);

    if (!exists) {
      throw new NotFoundException("Task not found");
    }

    repository.deleteById(id);
  }

  @GetMapping("/tasks")
  @ResponseStatus(HttpStatus.OK)
  public List<Task> listTasks(@RequestParam(name = "categoryId", required = true) String categoryId, @RequestParam(name = "page", required = false, defaultValue = "0") Integer page, @RequestParam(name = "size", required = false, defaultValue = "0") Integer size) {
    List<Task> tasks = null;
    if (size != 0) {
      tasks = repository.findAllByCategoryId(categoryId, PageRequest.of(page, size));
    }
    if (size == 0) {
      tasks = repository.findAllByCategoryId(categoryId);
    }
    return tasks;
  }

  @GetMapping("/tasks/count")
  @ResponseStatus(HttpStatus.OK)
  public Long countTasks(@RequestParam(name = "categoryId", required = true) String categoryId) {
    boolean exists = repository.existsByCategoryId(categoryId);

    if (!exists) {
      throw new NotFoundException("Task not found");
    }

    return repository.countByCategoryId(categoryId);
  }
}
