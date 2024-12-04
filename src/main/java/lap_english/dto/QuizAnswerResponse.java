package lap_english.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizAnswerResponse extends BaseDto {
    private String answer;
    private boolean isCorrect;
    private String imgAnswer;
}
