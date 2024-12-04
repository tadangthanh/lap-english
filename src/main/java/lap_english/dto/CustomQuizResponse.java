package lap_english.dto;

import lap_english.entity.TypeQuiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomQuizResponse extends BaseDto {
    private TypeQuiz typeQuiz;
    private String question;
    private String imageQuestion;
    private Long exerciseGrammarId;
    private List<QuizAnswerResponse> quizAnswers;
}
