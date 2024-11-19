package lap_english.controller;

import jakarta.validation.constraints.Min;
import lap_english.dto.MainTopicDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.IMainTopicService;
import lombok.RequiredArgsConstructor;
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
        return new ResponseData<>(HttpStatus.CREATED.value(), "Create main topic successfully", mainTopicService.create(mainTopicDto));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        mainTopicService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete main topic successfully", null);
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @Validated @RequestBody MainTopicDto mainTopicDto) {
        mainTopicDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Update main topic successfully", mainTopicService.update(mainTopicDto));
    }

    @GetMapping
    public ResponseData<?> getPage(   Pageable pageable,
                                      @RequestParam(required = false, value = "maintopic") String[] mainTopic) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", mainTopicService.advanceSearchBySpecification(pageable, mainTopic));
    }
    @GetMapping("/list")
    public ResponseData<?> getAll() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", mainTopicService.getAll());
    }
}
