package lap_english.controller;

import lap_english.dto.request.CustomQuizRequest;
import lap_english.dto.response.ResponseData;
import lap_english.service.ICustomQuizService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/custom-quiz")
@Validated
@RestController
public class CustomQuizController {
    private final ICustomQuizService customQuizService;

    @GetMapping("/exercise-grammar/{exerciseGrammarId}")
    public ResponseData<?> getByExerciseGrammar(@PathVariable("exerciseGrammarId") Long exerciseGrammarId) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", customQuizService.getByExerciseGrammarId(exerciseGrammarId));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteById(@PathVariable("id") Long id) {
        customQuizService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestBody CustomQuizRequest customQuizRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", customQuizService.save(customQuizRequest));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody CustomQuizRequest customQuizRequest) {
        customQuizRequest.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", customQuizService.update(customQuizRequest));
    }

}
