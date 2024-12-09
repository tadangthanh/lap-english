package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.MainTopicDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IMainTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-topic")
@Validated
public class MainTopicController {
    private final IMainTopicService mainTopicService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<MainTopicDto> create(@Validated @RequestBody MainTopicDto mainTopicDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Create main topic successfully", mainTopicService.create(mainTopicDto));
    }


    @Operation(summary = "delete theo id", description = "ko tra ve gi ca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 ", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable Long id) {
        mainTopicService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete main topic successfully", null);
    }

    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<MainTopicDto> update(@PathVariable Long id, @Validated @RequestBody MainTopicDto mainTopicDto) {
        mainTopicDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Update main topic successfully", mainTopicService.update(mainTopicDto));
    }


    @Operation(summary = "lấy page maintopic ", description = "Trả về danh sách  maintopic theo page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<MainTopicDto>>> getPage(
            @Parameter(description = "số trang, mặc định là 0", example = "{\n" +
                    "  \"page\": 0,\n" +
                    "  \"size\": 1,\n" +
                    "  \"sort\": [\n" +
                    "    \"id\"\n" +
                    "  ]\n" +
                    "}")
            Pageable pageable,
            @Parameter(
                    description = "search theo field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", format = "string", defaultValue = "name~thay giá trị cần tìm kiếm vào đây")
                    ))
            @RequestParam(required = false, value = "maintopic") String[] mainTopic) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", mainTopicService.advanceSearchBySpecification(pageable, mainTopic));
    }

    @Operation(summary = "lấy tat ca maintopic ", description = "Trả về danh sách  maintopic ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 list các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/list")
    public ResponseData<List<MainTopicDto>> getAll() {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", mainTopicService.getAll());
    }

    @Operation(summary = "mở khóa chủ đề chính", description = "Trả về đối tượng vừa mở khóa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa mở khóa "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/unlock/{id}")
    public ResponseData<MainTopicDto> unLock(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Unlock main topic successfully", mainTopicService.unLock(id));
    }
}
