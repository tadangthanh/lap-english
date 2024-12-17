package lap_english.mapper;

import lap_english.dto.UserDailyTaskDto;
import lap_english.entity.UserDailyTask;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)

public interface UserDailyTaskMapper {
    UserDailyTaskDto toDto(UserDailyTask userDailyTask);
    UserDailyTask toEntity(UserDailyTaskDto userDailyTaskDto);
}
