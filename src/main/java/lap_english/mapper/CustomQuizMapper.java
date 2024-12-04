package lap_english.mapper;

import lap_english.dto.CustomQuizResponse;
import lap_english.dto.request.CustomQuizRequest;
import lap_english.entity.CustomQuiz;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface CustomQuizMapper {

    CustomQuizResponse entityToResponse(CustomQuiz customQuiz);

    @Mapping(target = "imageQuestion", ignore = true)
    @Mapping(target = "quizAnswers",ignore = true)
    CustomQuiz requestToEntity(CustomQuizRequest customQuizRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imageQuestion", ignore = true)
    @Mapping(target = "quizAnswers",ignore = true)
    void updateFromRequest(CustomQuizRequest customQuizRequest, @MappingTarget CustomQuiz customQuiz);

}
