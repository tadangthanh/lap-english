package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.request.QuizAnswerRequest;
import lap_english.dto.response.CustomQuizResponse;
import lap_english.dto.response.QuizAnswerResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IQuizAnswerService;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/quiz-answer")
@Validated
public class QuizAnswerController {
    private final IQuizAnswerService quizAnswerService;

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<QuizAnswerResponse> create(@Validated(Create.class) @RequestBody QuizAnswerRequest quizAnswerRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", quizAnswerService.save(quizAnswerRequest));
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
        quizAnswerService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }

    @Operation(summary = "lấy danh sach QuizAnswer theo id cua CustomQuiz", description = "Trả về danh sach QuizAnswer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về danh sach QuizAnswer"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/custom-quiz/{id}")
    public ResponseData<List<QuizAnswerResponse>> getByQuizCustomId(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", quizAnswerService.getByQuizCustomId(id));
    }


    @Operation(summary = "Update theo id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<QuizAnswerResponse> update(@PathVariable("id")Long id,@Validated(Update.class) @RequestBody QuizAnswerRequest quizAnswerRequest) {
        quizAnswerRequest.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", quizAnswerService.update(quizAnswerRequest));
    }
}
