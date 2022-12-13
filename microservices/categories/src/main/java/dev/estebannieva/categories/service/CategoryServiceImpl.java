package dev.estebannieva.categories.service;

import com.google.protobuf.Empty;
import dev.estebannieva.categories.client.TaskGrpcClient;
import dev.estebannieva.categories.mapper.CategoryMapper;
import dev.estebannieva.categories.model.Category;
import dev.estebannieva.categories.repository.CategoryRepository;
import io.grpc.stub.StreamObserver;
import dev.estebannieva.categories.proto.CategoryServiceGrpc;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends CategoryServiceGrpc.CategoryServiceImplBase {

  private final CategoryRepository repository;

  private final TaskGrpcClient taskClient;

  public CategoryServiceImpl(CategoryRepository repository, TaskGrpcClient taskClient) {
    this.repository = repository;
    this.taskClient = taskClient;
  }

  @Override
  public void listCategories(Empty request, StreamObserver<dev.estebannieva.categories.proto.Category> responseObserver) {
    List<Category> all = repository.findAll();
    List<dev.estebannieva.categories.proto.Category> categories = CategoryMapper.INSTANCE.categoriesToCategories(all);

    for (int i = 0; i < categories.size(); i++) {
      long count = taskClient.countTasks(categories.get(i).getId());
      dev.estebannieva.categories.proto.Category mergedCategory = CategoryMapper.INSTANCE.countToCategory(count, categories.get(i));
      System.out.println("id: " + mergedCategory.getId());
      categories.set(i, mergedCategory);
    }

    categories.forEach(responseObserver::onNext);
    responseObserver.onCompleted();
  }
}
