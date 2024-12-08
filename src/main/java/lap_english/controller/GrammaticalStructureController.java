package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.GrammaticalStructureDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IGrammaticalStructureService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/api/v1/grammatical-structure")
public class GrammaticalStructureController {
    private final IGrammaticalStructureService grammaticalStructureService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<GrammaticalStructureDto> create(@Validated(Create.class) @RequestBody GrammaticalStructureDto grammaticalStructureDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", grammaticalStructureService.create(grammaticalStructureDto));
    }

    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<GrammaticalStructureDto> update(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody GrammaticalStructureDto grammaticalStructureDto) {
        grammaticalStructureDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammaticalStructureService.update(grammaticalStructureDto));
    }


    @Operation(summary = "delete theo id", description = "ko tra ve gi ca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 ", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> delete(@PathVariable("id") Long id) {
        grammaticalStructureService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }

    @Operation(summary = "lấy page gramtical structure ", description = "Trả về danh sách   gramtical structure  theo page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<GrammaticalStructureDto>>> advanceSearchBySpecification(
            @Parameter(description = "số trang, mặc định là 0", example = "{\n" +
                    "  \"page\": 0,\n" +
                    "  \"size\": 1,\n" +
                    "  \"sort\": [\n" +
                    "    \"id\"\n" +
                    "  ]\n" +
                    "}")
            Pageable pageable,
            @Parameter(
                    description = "search by field",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", format = "string",defaultValue = "structure~thay giá trị cần tìm kiếm vào đây")
                    ))
            @RequestParam(required = false, value = "grammaticalStructure") String[] grammaticalStructure) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammaticalStructureService.advanceSearchBySpecification(pageable, grammaticalStructure));
    }
}
