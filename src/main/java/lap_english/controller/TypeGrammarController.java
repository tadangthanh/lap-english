package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.TypeGrammarDto;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.ITypeGrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/type-grammar")
@Validated
public class TypeGrammarController {
    private final ITypeGrammarService typeGrammarService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<TypeGrammarDto> createTypeGrammar(@Validated @RequestBody TypeGrammarDto typeGrammarDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", typeGrammarService.create(typeGrammarDto));
    }
    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<TypeGrammarDto> updateTypeGrammar(@PathVariable Long id, @Validated @RequestBody TypeGrammarDto typeGrammarDto) {
        typeGrammarDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", typeGrammarService.update(typeGrammarDto));
    }
    @Operation(summary = "delete theo id", description = "ko tra ve gi ca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteTypeGrammar(@PathVariable Long id) {
        typeGrammarService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }
    @Operation(summary = "lay Type grammar theo id", description = "return doi tuong tim thay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, ko return gi het"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/{id}")
    public ResponseData<TypeGrammarDto> getTypeGrammar(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", typeGrammarService.findById(id));
    }
    @Operation(summary = "lấy theo trang, tìm kiếm theo trang ", description = "trả về 1 page các đối tượng  ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<?> getAllBySearch(  @Parameter(
            description = "số trang, số page,xắp xếp tăng giảm của page cần lấy, page bắt đầu từ 0",
            example = "?page=0&size=10&typeGrammars=name~thi hien tai don&sort=name,desc"
    )Pageable pageable,
                                            @Parameter(
                                                    description = "danh sách các field cần tìm kiếm ( có thể có nhiều field )",
                                                    example = "name~thi hien tai don, (tức là đang tìm kiếm nam có chứa từ thi hien tai don) "
                                            )
                                            @RequestParam(required = false, value = "typeGrammars") String[] typeGrammars) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", typeGrammarService.advanceSearchBySpecification(pageable, typeGrammars));
    }
}
