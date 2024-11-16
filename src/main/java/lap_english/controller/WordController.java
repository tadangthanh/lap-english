package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.WordDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.IWordService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/word")
@Validated
public class WordController {
    private final IWordService wordService;

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestPart("data") WordDto wordDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        wordDto.setFile(file);
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", wordService.create(wordDto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteWord(@PathVariable Long id) {
        wordService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated(Update.class) @RequestBody WordDto wordDto) {
        wordDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", wordService.update(wordDto));
    }

    @GetMapping("/sub-topic/{subtopicId}")
    public ResponseData<?> getBySubTopic(@PathVariable("subtopicId") Long subtopicId,
                                         @Min(0) @RequestParam(defaultValue = "0") int page,
                                         @Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", wordService.getBySubTopicId(subtopicId, page, size));
    }

}
