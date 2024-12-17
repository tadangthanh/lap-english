package lap_english.mapper;

import lap_english.dto.DailyTaskDto;
import lap_english.entity.DailyTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface DailyTaskMapper {
    @Mapping(target = "id", source = "id")
    DailyTaskDto toDto(DailyTask dailyTask);
    DailyTask toEntity(DailyTaskDto dailyTaskDto);
}
