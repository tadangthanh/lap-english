package lap_english.mapper;

import lap_english.dto.GrammarDto;
import lap_english.entity.Grammar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface GrammarMapper {
    @Mapping(target = "typeGrammarId", source = "typeGrammar.id")
    GrammarDto toDto(Grammar entity);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    Grammar toEntity(GrammarDto dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(GrammarDto dto, @MappingTarget Grammar entity);

}
