package lap_english.controller;

import lap_english.dto.request.QuizAnswerRequest;
import lap_english.dto.response.ResponseData;
import lap_english.service.IQuizAnswerService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/quiz-answer")
@Validated
public class QuizAnswerController {
    private final IQuizAnswerService quizAnswerService;

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestBody QuizAnswerRequest quizAnswerRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", quizAnswerService.save(quizAnswerRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        quizAnswerService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }

    @GetMapping("/custom-quiz/{id}")
    public ResponseData<?> getByQuizCustomId(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", quizAnswerService.getByQuizCustomId(id));
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable("id")Long id,@Validated(Update.class) @RequestBody QuizAnswerRequest quizAnswerRequest) {
        quizAnswerRequest.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", quizAnswerService.update(quizAnswerRequest));
    }
}
