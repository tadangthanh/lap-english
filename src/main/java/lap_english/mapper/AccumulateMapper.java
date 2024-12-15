package lap_english.mapper;

import lap_english.dto.AccumulateDto;
import lap_english.entity.Accumulate;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface AccumulateMapper {
    AccumulateDto toDto(Accumulate accumulate);
    Accumulate toEntity(AccumulateDto accumulateDto);
}
