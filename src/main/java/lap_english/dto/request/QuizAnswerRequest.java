package lap_english.dto.request;

import lap_english.dto.BaseDto;
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
    private String answer;
    private boolean correct;
    private MultipartFile imgAnswer;
//    private Long customQuizId;
}
