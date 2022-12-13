package dev.estebannieva.categories.mapper;

import dev.estebannieva.categories.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import dev.estebannieva.tasks.proto.CategoryId;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

  CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

  List<dev.estebannieva.categories.proto.Category> categoriesToCategories(List<Category> categories);

  CategoryId idToCategoryId(String id);

  dev.estebannieva.categories.proto.Category countToCategory(Long requestCount, @MappingTarget dev.estebannieva.categories.proto.Category resultCategoryMessage);

  @AfterMapping
  default dev.estebannieva.categories.proto.Category afterCountToCategory(@MappingTarget dev.estebannieva.categories.proto.Category resultCategoryMessage, Long requestCount) {
    return resultCategoryMessage.toBuilder().setCount(requestCount).build();
  }
}
