package lap_english.service;

import lap_english.dto.request.ExerciseGrammarRequest;
import lap_english.dto.response.ExerciseGrammarResponse;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface IExerciseGrammarService {
    void delete(Long id);

    ExerciseGrammarResponse save(ExerciseGrammarRequest exerciseGrammarRequest);

    PageResponse<?> getByGrammaticalStructureId(Long grammaticalStructureId, Integer page, Integer size);

}
