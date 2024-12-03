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
        try {
            return new ResponseData<>(HttpStatus.CREATED.value(), "Success", sentenceService.save(sentenceDto));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        try {
            sentenceService.delete(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success", null);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated(Update.class) @RequestBody SentenceDto sentenceDto) {
        try {
            sentenceDto.setId(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.update(sentenceDto));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseData<?> getAll(Pageable pageable,
                                  @RequestParam(required = false, value = "sentence") String[] sentence) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.advancedSearch(pageable, sentence));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/sub-topic/{subTopicId}")
    public ResponseData<?> getBySubTopicId(@PathVariable Long subTopicId,
                                           @Min(0) @RequestParam(defaultValue = "0") int page,
                                           @Min(1) @RequestParam(defaultValue = "10") int size) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.getBySubTopicId(subTopicId, page, size));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @PostMapping("/import/{subTopicId}")
    public ResponseData<?> importFromExcel(@PathVariable Long subTopicId, @RequestParam("file") MultipartFile file) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.importFromExcel(subTopicId, file));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }
}
