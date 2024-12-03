package lap_english.controller;

import lap_english.dto.TopicChatDto;
import lap_english.dto.response.ResponseData;
import lap_english.service.ITopicChatService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/topic-chat")
@Validated
public class TopicChatController {
    private final ITopicChatService topicChatService;

    @PostMapping
    public ResponseData<?> create(@RequestPart("data") @Validated(Create.class) TopicChatDto topicChatDto, @RequestPart(value = "file") MultipartFile file) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Success", topicChatService.save(topicChatDto, file));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(@PathVariable Long id) {
        topicChatService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete topic chat successfully", null);
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @RequestPart("data") @Validated(Update.class) TopicChatDto topicChatDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        topicChatDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", topicChatService.update(topicChatDto, file));
    }
}
