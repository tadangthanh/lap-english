package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.TopicChatDto;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
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
    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<TopicChatDto> create(@RequestPart("data") @Validated(Create.class) TopicChatDto topicChatDto,
                                             @Parameter(
                                                     content = @Content(mediaType = "multipart/form-data",
                                                        schema = @Schema(type = "string", format = "binary"))
                                             )
                                             @RequestPart(value = "file") MultipartFile file) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Success", topicChatService.save(topicChatDto, file));
    }
    @Operation(summary = "delete theo id", description = "ko tra ve gi ca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable Long id) {
        topicChatService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete topic chat successfully", null);
    }
    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable Long id, @RequestPart("data") @Validated(Update.class) TopicChatDto topicChatDto,
                                  @Parameter(
                                            content = @Content(mediaType = "multipart/form-data",
                                                    schema = @Schema(type = "string", format = "binary"))
                                  )
                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        topicChatDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", topicChatService.update(topicChatDto, file));
    }
}
