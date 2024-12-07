package lap_english.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.dto.BaseDto;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizAnswerRequest extends BaseDto {
    @NotBlank(message = "Answer is required", groups = {Create.class, Update.class})
    private String answer;
    @NotNull(message = "correct is required", groups = {Create.class, Update.class})
    private Boolean correct;
    private MultipartFile imgAnswer;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long customQuizId;
}
