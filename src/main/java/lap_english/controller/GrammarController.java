package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.GrammarDto;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IGrammarService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/grammar")
@RestController
@Validated
public class GrammarController {
    private final IGrammarService grammarService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<GrammarDto> create(@Validated(Create.class) @RequestBody GrammarDto dto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", grammarService.create(dto));
    }

    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<GrammarDto> update(@PathVariable Long id, @Validated(Update.class) @RequestBody GrammarDto dto) {
        dto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammarService.update(dto));
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
        grammarService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }

    @Operation(summary = "tim kiem  theo id", description = "tra ve doi tuong tim kiem dc")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/{id}")
    public ResponseData<GrammarDto> findById(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammarService.findById(id));
    }

    @Operation(summary = "lấy page Grammar ", description = "Trả về danh sách  Grammar theo page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<?> advanceSearchBySpecification(
            @Parameter(description = "số trang, mặc định là 0", example = "{\n" +
                    "  \"page\": 0,\n" +
                    "  \"size\": 1,\n" +
                    "  \"sort\": [\n" +
                    "    \"name,desc\"\n" +
                    "  ]\n" +
                    "}")
            Pageable pageable,
            @Parameter(
                    description = "tìm kiếm theo tên,mô tả, cấu trúc tuân thủ : <key><operation><value> " +
                            "+ trong đó key là tên trường cần tìm kiếm, " +
                            "+ value là giá trị cần tìm kiếm" +
                            "+ operation là toán tử so sánh bao gồm: ~ tìm kiếm tồn tại chuỗi, : tìm kiếm chính xác" +
                            "nếu cần tìm kiếm trên nhiều field thì tách nhau bởi dấu phẩy" ,
                    example = "name~thì hiện tại,description~cấu trúc",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string", format = "string",defaultValue = "sentence~thay giá trị cần tìm kiếm vào đây")
                    ))
            @RequestParam(required = false, value = "grammar") String[] grammar) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", grammarService.advanceSearchBySpecification(pageable, grammar));
    }
}
