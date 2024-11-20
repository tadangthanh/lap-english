package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.SentenceDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.ISentenceService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sentence")
@Validated
public class SentenceController {
    private final ISentenceService sentenceService;

    @PostMapping
    public ResponseData<?> create(@Validated(Create.class) @RequestBody SentenceDto sentenceDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Success", sentenceService.save(sentenceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        sentenceService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success", null);
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated(Update.class) @RequestBody SentenceDto sentenceDto) {
        sentenceDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.update(sentenceDto));
    }

    @GetMapping
    public ResponseData<?> getAll(Pageable pageable,
                                  @RequestParam(required = false, value = "sentence") String[] sentence) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.advancedSearch(pageable, sentence));
    }

    @GetMapping("/sub-topic/{subTopicId}")
    public ResponseData<?> getBySubTopicId(@PathVariable Long subTopicId,
                                           @Min(0) @RequestParam(defaultValue = "0") int page,
                                           @Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.getBySubTopicId(subTopicId, page, size));
    }
    @PostMapping("/import/{subTopicId}")
    public ResponseData<?> importFromExcel(@PathVariable Long subTopicId, @RequestParam("file") MultipartFile file) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.importFromExcel(subTopicId, file));
    }
}
