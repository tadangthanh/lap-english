package lap_english.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lap_english.dto.BaseDto;
import lap_english.validation.Create;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseGrammarRequest extends BaseDto {
    @NotNull(message = "Grammatical structure id is required", groups = {Create.class})
    private Long grammaticalStructureId;
    @Valid
    private CustomQuizRequest customQuiz;
}
