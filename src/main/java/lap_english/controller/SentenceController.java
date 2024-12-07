package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import lap_english.dto.SentenceDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.ISentenceService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sentence")
@Validated
public class SentenceController {
    private final ISentenceService sentenceService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<SentenceDto> create(@Validated(Create.class) @RequestBody SentenceDto sentenceDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Success", sentenceService.save(sentenceDto));
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
        sentenceService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Success", null);
    }


    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<SentenceDto> update(@PathVariable Long id, @Validated(Update.class) @RequestBody SentenceDto sentenceDto) {
        sentenceDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.update(sentenceDto));
    }


    @Operation(summary = "lấy page sentence ", description = "Trả về danh sách  sentence theo page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<SentenceDto>>> getAll(
            @Parameter(description = "số trang, mặc định là 0", example = "{\n" +
                    "  \"page\": 0,\n" +
                    "  \"size\": 1,\n" +
                    "  \"sort\": [\n" +
                    "    \"sentence\"\n" +
                    "  ]\n" +
                    "}")
            Pageable pageable,
            @Parameter(
                    description = "xắp xếp theo trường nào, mặc định là sentence, tăng dần, giảm dần bằng asc, desc",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", format = "string",defaultValue = "sentence~thay giá trị cần tìm kiếm vào đây")
                    ))
                                  @RequestParam(required = false, value = "sentence") String[] sentence) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.advancedSearch(pageable, sentence));
    }

    @Operation(summary = "lấy page sentence theo id của sub topic  ", description = "Trả về danh sách sentence theo page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/sub-topic/{subTopicId}")
    public ResponseData<PageResponse<List<SentenceDto>>> getBySubTopicId(@PathVariable Long subTopicId,
                                           @Min(0) @RequestParam(defaultValue = "0") int page,
                                           @Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Success", sentenceService.getBySubTopicId(subTopicId, page, size));

    }

    @Operation(summary = "import file excel theo dinh dang quy dinh", description = "không trả về gì cả, chỉ thông báo xử lý file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 ", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/import/{subTopicId}")
    public ResponseData<CompletableFuture<Integer>> importFromExcel(@PathVariable Long subTopicId, @RequestParam("file") MultipartFile file) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Success", sentenceService.importFromExcel(subTopicId, file));
    }
}
