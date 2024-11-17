package lap_english.mapper;

import lap_english.dto.UserDto;
import lap_english.entity.User;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface UserMapper {
    UserDto toDto(User entity);

    User toEntity(UserDto dto);

}