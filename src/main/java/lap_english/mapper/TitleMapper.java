package lap_english.mapper;

import lap_english.dto.TitleDto;
import lap_english.entity.Title;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface TitleMapper {
    @Mapping(target = "task",ignore = true)
    @Mapping(target = "reward",ignore = true)
    TitleDto toDto(Title title);

    @Mapping(target = "task",ignore = true)
    @Mapping(target = "reward",ignore = true)
    Title toEntity(TitleDto titleDto);

    void updateFromDto(TitleDto titleDto, @MappingTarget Title title);
}
