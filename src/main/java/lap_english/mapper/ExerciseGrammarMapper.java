package lap_english.mapper;

import lap_english.dto.response.ExerciseGrammarResponse;
import lap_english.entity.ExerciseGrammar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface ExerciseGrammarMapper {

    @Mapping(target = "grammaticalStructureId", source = "grammaticalStructure.id")
    ExerciseGrammarResponse entityToResponse(ExerciseGrammar exerciseGrammar);

    ExerciseGrammar responseToEntity(ExerciseGrammarResponse exerciseGrammarResponse);
}
