package lap_english.mapper;

import lap_english.dto.TypeGrammarDto;
import lap_english.entity.TypeGrammar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface TypeGrammarMapper {
    TypeGrammarDto toDto(TypeGrammar entity);

    @Mapping(target = "id", ignore = true)
    TypeGrammar toEntity(TypeGrammarDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TypeGrammarDto dto, @MappingTarget TypeGrammar entity);
}
