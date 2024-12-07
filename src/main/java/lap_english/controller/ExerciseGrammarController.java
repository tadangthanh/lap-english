package lap_english.controller;

import lap_english.dto.request.ExerciseGrammarRequest;
import lap_english.dto.response.ResponseData;
import lap_english.service.IExerciseGrammarService;
import lap_english.validation.Create;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exercise-grammar")
@Validated
public class ExerciseGrammarController {
    private final IExerciseGrammarService exerciseGrammarService;

    @PostMapping
    public ResponseData<?> save(@Validated(Create.class) @RequestBody ExerciseGrammarRequest exerciseGrammarRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", exerciseGrammarService.save(exerciseGrammarRequest));
    }

    @GetMapping("/grammatical-structure/{grammaticalStructureId}")
    public ResponseData<?> getByGrammaticalStructureId(@PathVariable("grammaticalStructureId") Long grammaticalStructureId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", exerciseGrammarService.getByGrammaticalStructureId(grammaticalStructureId, page, size));
    }
    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable("id") Long id) {
        exerciseGrammarService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }
}

