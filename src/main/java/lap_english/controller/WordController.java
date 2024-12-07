package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Null;
import lap_english.dto.WordDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IWordService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/word")
@Validated
public class WordController {
    private final IWordService wordService;
    @Operation(summary = "Tạo mới 1 word", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 ", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<WordDto> create(@Validated(Create.class) @RequestPart("data") WordDto wordDto, @RequestPart(value = "file", required = false) MultipartFile file) {
        wordDto.setFile(file);
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", wordService.create(wordDto));
    }
    @Operation(summary = "Xóa word theo id ", description = "không return về gì")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Thành công, ko return giá trị"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteWord(@PathVariable Long id) {
        wordService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }
    @Operation(summary = "update word theo id ", description = "trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng vừa update"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<WordDto> update(@PathVariable Long id, @Validated(Update.class) @RequestBody WordDto wordDto) {
        wordDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", wordService.update(wordDto));
    }


    @Operation(summary = "lấy danh sách word theo subtopicId truyền vào ", description = "trả về 1 page  các đối tượng  ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page word "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<WordDto>>> getBySubTopic(
            @Parameter(
                    description = "số trang, số page,xắp xếp tăng giảm của page cần lấy, page bắt đầu từ 0",
                    example = "?page=0&size=10&word=subTopic.id:2,word~hello&sort=word,desc"
            )
            Pageable pageable,

                                                          @Parameter(
                                                                  description = "danh sách các field cần tìm kiếm ( có thể có nhiều field )",
                                                                  example = "word~orange, (tức là đang tìm kiếm word có chứa từ orange) "
                                                          )
                                                          @RequestParam(required = false, value = "word") String[] word) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", wordService.advanceSearchBySpecification(pageable,word));
    }

    @Operation(summary = "import file excel theo dinh dang quy dinh", description = "không trả về gì cả, chỉ thông báo xử lý file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 ", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/import/{subTopicId}")
    public ResponseData<CompletableFuture<Integer>> importWordExcel(@PathVariable @Min(1) Long subTopicId, @RequestPart("file") MultipartFile file) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Processing file... Please wait.", wordService.importFromExcel(subTopicId, file));
    }

}
