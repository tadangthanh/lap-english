package lap_english.service;

import lap_english.dto.QuizAnswerResponse;
import lap_english.dto.request.QuizAnswerRequest;

import java.util.List;

public interface IQuizAnswerService {
    void delete(Long id);

    QuizAnswerResponse save(QuizAnswerRequest quizAnswerRequest, Long quizCustomId);

    List<QuizAnswerResponse> getByQuizCustomId(Long id);

    QuizAnswerResponse update(QuizAnswerRequest quizAnswerRequest);

}
