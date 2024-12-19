package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.request.CustomQuizRequest;
import lap_english.dto.response.CustomQuizResponse;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.ICustomQuizService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/custom-quiz")
@Validated
@RestController
public class CustomQuizController {
    private final ICustomQuizService customQuizService;

    @Operation(summary = "Lấy danh sách câu hỏi theo bài ngữ pháp", description = "Trả về danh sách câu hỏi của bài ngữ pháp dựa trên ID được cung cấp.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về danh sách câu hỏi."),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/exercise-grammar/{exerciseGrammarId}")
    public ResponseData<CustomQuizResponse> getByExerciseGrammar(@PathVariable("exerciseGrammarId") Long exerciseGrammarId) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", customQuizService.getByExerciseGrammarId(exerciseGrammarId));
    }

    @Operation(summary = "xóa câu hỏi theo id", description = "Xóa câu hỏi dựa trên ID được cung cấp.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Thành công, không trả về gì "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteById(@PathVariable("id") Long id) {
        customQuizService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", null);
    }

    @Operation(summary = "Thêm câu hỏi", description = "thêm câu hỏi mới")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Thành công, trả về câu hỏi mới vừa thêm ."),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đối tượng liên quan "),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<CustomQuizResponse> create(@Validated(Create.class) @ModelAttribute  CustomQuizRequest customQuizRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", customQuizService.save(customQuizRequest));
    }

    @Operation(summary = "Cập nhật  câu hỏi dựa theo id", description = "thêm câu hỏi mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về câu hỏi mới vừa thêm ."),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy đối tượng liên quan "),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<?> update(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody CustomQuizRequest customQuizRequest) {
        customQuizRequest.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", customQuizService.update(customQuizRequest));
    }


    @Operation(summary = "lấy page quiz ", description = "Trả về danh sách  quiz theo page")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<CustomQuizResponse>>> getPage(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", customQuizService.getPage(page, size));
    }



    @Operation(summary = "lấy page theo id ", description = "Trả về  quiz theo")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Thành công, trả về 1 page các đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/{id}")
    public ResponseData<CustomQuizResponse> getById(@PathVariable("id") Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Get main topic successfully", customQuizService.getById(id));
    }

}
