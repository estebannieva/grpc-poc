package dev.estebannieva.categories;

import dev.estebannieva.categories.mapper.CategoryMapper;
import dev.estebannieva.categories.model.Category;
import dev.estebannieva.categories.repository.CategoryRepository;
import dev.estebannieva.categories.service.CategoryServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.mapstruct.factory.Mappers;
import dev.estebannieva.tasks.proto.TaskServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
public class CategoriesApplication {

  private final CategoryRepository repository;

  private final CategoryServiceImpl service;

  @Value("${grpc-port}") private int grpcPort;

  @Value("${grpc-tasks-host}") private String grpcTasksHost;

  @Value("${grpc-tasks-port}") private int grpcTasksPort;

  public CategoriesApplication(CategoryRepository repository, @Lazy CategoryServiceImpl service) {
    this.repository = repository;
    this.service = service;
  }

  public static void main(String[] args) {
    SpringApplication.run(CategoriesApplication.class, args);
  }

  private void seed() {
    String[] categoryNames = {"Personal", "Work"};

    for (String categoryName : categoryNames) {
      if (!repository.existsByName(categoryName)) {
        Category category = new Category();
        category.setName(categoryName);

        repository.save(category);
        System.out.println("Category created: " + category.getId());
      }
    }
  }

  private void startServer() throws IOException, InterruptedException {
    Server server = ServerBuilder
        .forPort(grpcPort)
        .addService(service)
        .addService(ProtoReflectionService.newInstance())
        .build();
    server.start();
    System.out.println("Server started, listening on " + grpcPort);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("*** shutting down gRPC server since JVM is shutting down");
      server.shutdown();
      System.out.println("*** server shut down");
    }));

    server.awaitTermination();
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      seed();
      startServer();
    };
  }

  @Bean
  public CategoryMapper categoryMapper() {
    return Mappers.getMapper(CategoryMapper.class);
  }

  @Bean
  public ManagedChannel managedChannel() {
    return ManagedChannelBuilder.forAddress(grpcTasksHost, grpcTasksPort)
        .usePlaintext()
        .build();
  }

  @Bean
  public TaskServiceGrpc.TaskServiceBlockingStub taskServiceBlockingStub(ManagedChannel channel) {
    return TaskServiceGrpc.newBlockingStub(channel);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
