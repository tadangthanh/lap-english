package lap_english.dto.request;

import lap_english.dto.BaseDto;
import lap_english.entity.TypeQuiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomQuizRequest extends BaseDto {
    private TypeQuiz typeQuiz;
    private String question;
    private MultipartFile imageQuestion;
//    private Long exerciseGrammarId;
    private List<QuizAnswerRequest> quizAnswers;
}
