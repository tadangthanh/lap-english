package lap_english.mapper;

import lap_english.dto.UserTitleDto;
import lap_english.entity.UserTitle;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface UserTitleMapper {
    UserTitleDto toDto(UserTitleDto userTitleDto);

    UserTitle toEntity(UserTitle userTitle);
}
