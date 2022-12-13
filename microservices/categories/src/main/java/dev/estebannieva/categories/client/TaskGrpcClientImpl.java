package dev.estebannieva.categories.client;

import dev.estebannieva.categories.mapper.CategoryMapper;
import io.grpc.StatusRuntimeException;
import dev.estebannieva.tasks.proto.TaskCount;
import dev.estebannieva.tasks.proto.TaskServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class TaskGrpcClientImpl implements TaskGrpcClient {

    private final TaskServiceGrpc.TaskServiceBlockingStub stub;

    public TaskGrpcClientImpl(TaskServiceGrpc.TaskServiceBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public long countTasks(String categoryId) {
        try {
            TaskCount taskCount = stub.countTasks(CategoryMapper.INSTANCE.idToCategoryId(categoryId));
            System.out.println("count: " + taskCount.getCount() + " categoryId: " + categoryId);
            return taskCount.getCount();
        } catch (StatusRuntimeException e) {
            System.out.println("RPC failed: {0}" + e.getStatus());
        }
        return 0;
    }
}
