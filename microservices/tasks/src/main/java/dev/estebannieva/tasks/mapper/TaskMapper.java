package dev.estebannieva.tasks.mapper;

import dev.estebannieva.tasks.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import dev.estebannieva.tasks.proto.TaskCount;
import dev.estebannieva.tasks.proto.TaskId;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

  TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

  Task taskMessageToTask(dev.estebannieva.tasks.proto.Task taskMessage);

  TaskId taskToTaskId(Task task);

  @Mapping(target = "targetTask.id", source = "id")
  Task mergeTask(String id, Task task, @MappingTarget Task targetTask);

  List<dev.estebannieva.tasks.proto.Task> tasksToTaskMessages(List<Task> tasks);

  TaskCount countToTaskCount(Long count);
}
