package lap_english.service;

import lap_english.dto.response.CustomQuizResponse;
import lap_english.dto.request.CustomQuizRequest;
import lap_english.dto.response.PageResponse;

import java.util.List;

public interface ICustomQuizService {
    CustomQuizResponse save(CustomQuizRequest customQuizRequest);

    CustomQuizResponse update(CustomQuizRequest customQuizRequest);
    CustomQuizResponse getById(Long id);


    void delete(Long id);

    CustomQuizResponse getByExerciseGrammarId(Long exerciseGrammarId);

    void deleteByExerciseGrammarId(Long exerciseGrammarId);

    PageResponse<List<CustomQuizResponse>> getPage(Integer page, Integer size);

}
