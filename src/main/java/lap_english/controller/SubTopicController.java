package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import lap_english.dto.SubTopicDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.ISubTopicService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-topic")
@Validated
public class SubTopicController {
    private final ISubTopicService subTopicService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<?> createSubTopic(@RequestPart("data") @Validated(Create.class) SubTopicDto subTopicDto,
                                          @Parameter(content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary")), example = "file") @RequestPart(value = "file", required = false) MultipartFile file) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", subTopicService.create(subTopicDto, file));

    }

    @Operation(summary = "delete theo id", description = "ko tra ve gi ca")
    @ApiResponses(value = {@ApiResponse(responseCode = "204 ", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteSubTopic(@PathVariable Long id) {
        subTopicService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete sub topic successfully", null);
    }

    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<SubTopicDto> updateSubTopic(@PathVariable Long id, @RequestPart("data") @Validated(Update.class) SubTopicDto subTopicDto, @Parameter(content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))) @RequestPart(value = "file", required = false) MultipartFile file) {
        subTopicDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", subTopicService.update(subTopicDto, file));
    }


    @Operation(summary = "lấy page subtopic ", description = "Trả về danh sách  subtopic theo page")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<SubTopicDto>>> getPage(@Parameter(description = "số trang, mặc định là 0", example = "{\n" + "  \"page\": 0,\n" + "  \"size\": 1,\n" + "  \"sort\": [\n" + "    \"name\"\n" + "  ]\n" + "}") Pageable pageable, @Parameter(description = "xắp xếp theo trường nào, mặc định là id, tăng dần, giảm dần bằng asc, desc", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", format = "string", defaultValue = "name~animal"))) @RequestParam(required = false, value = "subtopic") String[] subTopic) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", subTopicService.advanceSearchBySpecification(pageable, subTopic));
    }

    @Operation(summary = "lấy page subtopic theo id của main topic  ", description = "Trả về danh sách  subtopic theo page")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "), @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))), @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"), @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/main-topic/{mainTopicId}")
    public ResponseData<PageResponse<List<SubTopicDto>>> getByMainTopic(@PathVariable("mainTopicId") Long mainTopicId, @Min(0) @RequestParam(defaultValue = "0") int page, @Min(1) @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", subTopicService.getByMainTopicId(mainTopicId, page, size));

    }

    @Operation(summary = "lấy page subtopic theo id", description = "Trả về subtopic ")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "), @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))), @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"), @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/{id}")
    public ResponseData<SubTopicDto> getById(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get sub topic successfully", subTopicService.getById(id));
    }

    @Operation(summary = "Hoàn thành 1 subtopic ", description = "Trả về subtopic đã hoàn thành")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, Trả về subtopic đã hoàn thành"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))), @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"), @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}/complete")
    public ResponseData<SubTopicDto> completeSubTopic(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get sub topic successfully", subTopicService.complete(id));
    }

    @Operation(summary = "Mở khóa subtopic ", description = "Trả về subtopic đã mở khóa")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, Trả về subtopic đã mở khóa"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))), @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"), @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/unlock/{id}")
    public ResponseData<Boolean> unlockSubTopic(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get sub topic successfully", subTopicService.unlock(id));
    }

}
