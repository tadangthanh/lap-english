package lap_english.controller;

import lap_english.dto.MainTopicDto;
import lap_english.dto.response.ResponseData;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceNotFoundException;
import lap_english.service.IMainTopicService;
import lombok.RequiredArgsConstructor;
import org.json.HTTP;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-topic")
@Validated
public class MainTopicController {
    private final IMainTopicService mainTopicService;


    @PostMapping
    public ResponseData<?> create(@Validated @RequestBody MainTopicDto mainTopicDto) {
        try {
            return new ResponseData<>(HttpStatus.CREATED.value(), "Create main topic successfully", mainTopicService.create(mainTopicDto));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        try {
            mainTopicService.delete(id);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete main topic successfully", null);
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated @RequestBody MainTopicDto mainTopicDto) {
        try {
            mainTopicDto.setId(id);
            return new ResponseData<>(HttpStatus.OK.value(), "Update main topic successfully", mainTopicService.update(mainTopicDto));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseData<?> getPage(Pageable pageable,
                                   @RequestParam(required = false, value = "maintopic") String[] mainTopic) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", mainTopicService.advanceSearchBySpecification(pageable, mainTopic));
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/list")
    public ResponseData<?> getAll() {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", mainTopicService.getAll());
        } catch (Exception e) {
            return new ResponseData<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
    }
}
