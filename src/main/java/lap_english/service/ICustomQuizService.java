package lap_english.service;

import lap_english.dto.CustomQuizResponse;
import lap_english.dto.request.CustomQuizRequest;

public interface ICustomQuizService {
    CustomQuizResponse save(CustomQuizRequest customQuizRequest,Long exerciseGrammarId);

    CustomQuizResponse update(CustomQuizRequest customQuizRequest);

    void delete(Long id);

}
