package lap_english.dto.request;

import lap_english.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseGrammarRequest extends BaseDto {
    private Long grammaticalStructureId;
    private CustomQuizRequest customQuiz;
}
