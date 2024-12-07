package lap_english.mapper;

import lap_english.dto.response.QuizAnswerResponse;
import lap_english.dto.request.QuizAnswerRequest;
import lap_english.entity.QuizAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface QuizAnswerMapper {


    QuizAnswerResponse entityToResponse(QuizAnswer quizAnswer);

    @Mapping(target = "imgAnswer", ignore = true)
    QuizAnswer requestToEntity(QuizAnswerRequest quizAnswerRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imgAnswer", ignore = true)
    void updateFromRequest(QuizAnswerRequest quizAnswerRequest, @MappingTarget QuizAnswer quizAnswer);

    List<QuizAnswerResponse> entityListToResponseList(List<QuizAnswer> quizAnswers);

}
