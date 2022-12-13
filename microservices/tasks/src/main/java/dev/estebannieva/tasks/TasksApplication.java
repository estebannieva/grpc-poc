package dev.estebannieva.tasks;

import dev.estebannieva.tasks.mapper.TaskMapper;
import dev.estebannieva.tasks.service.TaskServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@SpringBootApplication
public class TasksApplication {

  private final TaskServiceImpl service;

  @Value("${grpc-port}") private int grpcPort;

  public TasksApplication(@Lazy TaskServiceImpl service) {
    this.service = service;
  }

  public static void main(String[] args) {
    SpringApplication.run(TasksApplication.class, args);
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
      startServer();
    };
  }

  @Bean
  public TaskMapper taskMapper() {
    return TaskMapper.INSTANCE;
  }
}
