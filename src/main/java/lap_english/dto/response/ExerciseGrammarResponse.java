package lap_english.dto.response;

import lap_english.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseGrammarResponse extends BaseDto {
    private Long grammaticalStructureId;
    private CustomQuizResponse customQuiz;
}
