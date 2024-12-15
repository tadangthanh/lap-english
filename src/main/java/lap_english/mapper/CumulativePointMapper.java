package lap_english.mapper;

import lap_english.dto.CumulativePointDto;
import lap_english.entity.CumulativePoint;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface CumulativePointMapper {
    CumulativePointDto toDto(CumulativePoint cumulativePoint);
    CumulativePoint toEntity(CumulativePointDto cumulativePointDto);
}
