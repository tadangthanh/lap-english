package lap_english.mapper;

import lap_english.dto.TaskDto;
import lap_english.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface TaskMapper {
    @Mapping(target = "id", source = "id")
    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    void updateFromDto(TaskDto taskDto,@MappingTarget Task task);
}
