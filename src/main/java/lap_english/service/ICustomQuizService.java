package lap_english.service;

import lap_english.dto.response.CustomQuizResponse;
import lap_english.dto.request.CustomQuizRequest;

public interface ICustomQuizService {
    CustomQuizResponse save(CustomQuizRequest customQuizRequest);

    CustomQuizResponse update(CustomQuizRequest customQuizRequest);

    void delete(Long id);

    CustomQuizResponse getByExerciseGrammarId(Long exerciseGrammarId);

    void deleteByExerciseGrammarId(Long exerciseGrammarId);


}
