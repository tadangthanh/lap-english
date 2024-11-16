package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.SubTopicDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.ISubTopicService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-topic")
@Validated
public class SubTopicController {
    private final ISubTopicService subTopicService;

    @PostMapping
    public ResponseData<?> createSubTopic(@RequestPart("data") @Validated(Create.class) SubTopicDto subTopicDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        subTopicDto.setFile(file);
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", subTopicService.create(subTopicDto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteSubTopic(@PathVariable Long id) {
        subTopicService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateSubTopic(@PathVariable Long id, @RequestPart("data") @Validated(Update.class) SubTopicDto subTopicDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        subTopicDto.setId(id);
        subTopicDto.setFile(file);
        return new ResponseData<>(HttpStatus.OK.value(), "success", subTopicService.update(subTopicDto));
    }

    @GetMapping
    public ResponseData<?> getAll(@Min(0) @RequestParam(defaultValue = "0") int page, @Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", subTopicService.getAll(page, size));
    }

    @GetMapping("/main-topic/{mainTopicId}")
    public ResponseData<?> getByMainTopic(@PathVariable("mainTopicId") Long mainTopicId, @Min(0) @RequestParam(defaultValue = "0") int page, @Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", subTopicService.getByMainTopicId(mainTopicId, page, size));
    }


}
