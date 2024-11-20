package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.WordDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.IWordService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated(Update.class) @RequestBody WordDto wordDto) {
        wordDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", wordService.update(wordDto));
    }

    @GetMapping
    public ResponseData<?> getBySubTopic(Pageable pageable,
                                          @RequestParam(required = false, value = "word") String[] word) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", wordService.advanceSearchBySpecification(pageable,word));
    }
    @PostMapping("/import/{subTopicId}")
    public ResponseData<?> importWordExcel(@PathVariable @Min(1) Long subTopicId, @RequestPart("file") MultipartFile file) {
        return new ResponseData<>(HttpStatus.OK.value(), "Processing file... Please wait.", wordService.importFromExcel(subTopicId, file));
    }

}
