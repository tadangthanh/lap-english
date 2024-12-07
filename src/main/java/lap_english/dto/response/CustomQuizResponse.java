package lap_english.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lap_english.entity.TypeQuiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomQuizResponse implements Serializable {
    private Long id;
    private TypeQuiz typeQuiz;
    private String question;
    private String imageQuestion;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long exerciseGrammarId;
    private List<QuizAnswerResponse> quizAnswers;
}
