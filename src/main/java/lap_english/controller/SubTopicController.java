package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.SubTopicDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.ISubTopicService;
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
@RequestMapping("/api/v1/sub-topic")
@Validated
public class SubTopicController {
    private final ISubTopicService subTopicService;

    @PostMapping
    public ResponseData<?> createSubTopic(@RequestPart("data") @Validated(Create.class) SubTopicDto subTopicDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            return new ResponseData<>(HttpStatus.CREATED.value(), "success", subTopicService.create(subTopicDto, file));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteSubTopic(@PathVariable Long id) {
        try {
            subTopicService.delete(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete sub topic successfully", null);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseData<?> updateSubTopic(@PathVariable Long id, @RequestPart("data") @Validated(Update.class) SubTopicDto subTopicDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            subTopicDto.setId(id);
            return new ResponseData<>(HttpStatus.OK.value(), "success", subTopicService.update(subTopicDto, file));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseData<?> getPage(
            Pageable pageable,
            @RequestParam(required = false, value = "subtopic") String[] subTopic
    ) {
        //?page=0&size=10&sort=id,desc&subtopic=name~d
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", subTopicService.advanceSearchBySpecification(pageable, subTopic));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/main-topic/{mainTopicId}")
    public ResponseData<?> getByMainTopic(@PathVariable("mainTopicId") Long mainTopicId,
                                          @Min(0) @RequestParam(defaultValue = "0") int page,
                                          @Min(1) @RequestParam(defaultValue = "10") int size) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", subTopicService.getByMainTopicId(mainTopicId, page, size));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/{id}")
    public ResponseData<?> getById(@PathVariable Long id) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get sub topic successfully", subTopicService.getById(id));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

}
