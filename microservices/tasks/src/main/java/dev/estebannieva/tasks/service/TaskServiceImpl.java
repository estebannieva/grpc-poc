package dev.estebannieva.tasks.service;

import com.google.protobuf.Empty;
import dev.estebannieva.tasks.mapper.TaskMapper;
import dev.estebannieva.tasks.repository.TaskRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import dev.estebannieva.tasks.proto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl extends TaskServiceGrpc.TaskServiceImplBase {

  private final TaskRepository repository;

  public TaskServiceImpl(TaskRepository repository) {
    this.repository = repository;
  }

  @Override
  public void createTask(dev.estebannieva.tasks.proto.Task request, StreamObserver<TaskId> responseObserver) {
    dev.estebannieva.tasks.model.Task savedTask;

    try {
      savedTask = repository.save(TaskMapper.INSTANCE.taskMessageToTask(request));
      System.out.println("Task created: " + savedTask);
    } catch (IllegalArgumentException e) {
      responseObserver.onError(Status.INTERNAL.withDescription(e.getLocalizedMessage()).asRuntimeException());
      return;
    }

    TaskId taskId = TaskMapper.INSTANCE.taskToTaskId(savedTask);
    responseObserver.onNext(taskId);
    responseObserver.onCompleted();
  }

  @Override
  public void updateTask(dev.estebannieva.tasks.proto.Task request, StreamObserver<Empty> responseObserver) {
    if (request.getId().isEmpty()) {
      responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Task id is required").asRuntimeException());
      return;
    }

    Optional<dev.estebannieva.tasks.model.Task> task = repository.findById(request.getId());
    if (task.isEmpty()) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("Task not found").asRuntimeException());
      return;
    }

    dev.estebannieva.tasks.model.Task updatedTask = TaskMapper.INSTANCE.mergeTask(request.getId(), TaskMapper.INSTANCE.taskMessageToTask(request), task.get());
    try {
      repository.save(updatedTask);
      System.out.println("Task updated: " + updatedTask);
    } catch (IllegalArgumentException e) {
      responseObserver.onError(Status.INTERNAL.withDescription(e.getLocalizedMessage()).asRuntimeException());
      return;
    }

    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void deleteTask(TaskId request, StreamObserver<Empty> responseObserver) {
    if (request.getId().isEmpty()) {
      responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Task id is required").asRuntimeException());
      return;
    }

    Optional<dev.estebannieva.tasks.model.Task> task = repository.findById(request.getId());
    if (task.isEmpty()) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("Task not found").asRuntimeException());
      return;
    }

    repository.deleteById(request.getId());
    System.out.println("Task deleted: " + task.get());

    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void listTasks(Category request, StreamObserver<dev.estebannieva.tasks.proto.Task> responseObserver) {
    if (request.getId().isEmpty()) {
      responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Category id is required").asRuntimeException());
      return;
    }

    boolean exists = repository.existsByCategoryId(request.getId());
    if (!exists) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("Category id not found").asRuntimeException());
      return;
    }

    List<dev.estebannieva.tasks.model.Task> tasks = null;
    if (request.getSize() != 0) {
      tasks = repository.findAllByCategoryId(request.getId(), PageRequest.of(request.getPage(), request.getSize()));
    }
    if (request.getSize() == 0) {
      tasks = repository.findAllByCategoryId(request.getId());
    }
    System.out.println("Listing tasks: " + tasks.size());
    List<dev.estebannieva.tasks.proto.Task> taskMessages = TaskMapper.INSTANCE.tasksToTaskMessages(tasks);
    taskMessages.forEach(responseObserver::onNext);
    responseObserver.onCompleted();
  }

  @Override
  public void countTasks(CategoryId request, StreamObserver<TaskCount> responseObserver) {
    if (request.getId().isEmpty()) {
      responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Category id is required").asRuntimeException());
      return;
    }

    boolean exists = repository.existsByCategoryId(request.getId());
    if (!exists) {
      responseObserver.onError(Status.NOT_FOUND.withDescription("Category id not found").asRuntimeException());
      return;
    }

    long count = repository.countByCategoryId(request.getId());
    System.out.println("Category " + request.getId() + " has " + count + " tasks");

    TaskCount taskCount = TaskMapper.INSTANCE.countToTaskCount(count);
    responseObserver.onNext(taskCount);
    responseObserver.onCompleted();
  }
}
