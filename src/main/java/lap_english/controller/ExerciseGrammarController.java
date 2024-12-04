package lap_english.controller;

import lap_english.dto.request.ExerciseGrammarRequest;
import lap_english.dto.response.ResponseData;
import lap_english.service.IExerciseGrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exercise-grammar")
public class ExerciseGrammarController {
    private final IExerciseGrammarService exerciseGrammarService;

    @PostMapping
    public ResponseData<?> save(@RequestBody ExerciseGrammarRequest exerciseGrammarRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", exerciseGrammarService.save(exerciseGrammarRequest));
    }
}
