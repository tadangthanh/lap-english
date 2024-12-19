package lap_english.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.dto.BaseDto;
import lap_english.entity.TypeQuiz;
import lap_english.validation.Create;
import lap_english.validation.Update;
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
    @NotNull(message = "Type quiz is required", groups = {Create.class, Update.class})
    private TypeQuiz typeQuiz;
    @NotBlank(message = "Question is required", groups = {Create.class, Update.class})
    private String question;
    private MultipartFile imageQuestion;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    private Long exerciseGrammarId;
    @Valid
    private List<QuizAnswerRequest> quizAnswers;
}
