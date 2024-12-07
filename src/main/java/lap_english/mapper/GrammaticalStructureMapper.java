package lap_english.mapper;


import lap_english.dto.GrammaticalStructureDto;
import lap_english.entity.GrammaticalStructure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface GrammaticalStructureMapper {
    @Mapping(target = "grammarId", source = "grammar.id")
    GrammaticalStructureDto toDto(GrammaticalStructure entity);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "structure", source = "structure")
    GrammaticalStructure toEntity(GrammaticalStructureDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "grammar", ignore = true)
    void updateFromDto(GrammaticalStructureDto dto, @MappingTarget GrammaticalStructure entity);

}
