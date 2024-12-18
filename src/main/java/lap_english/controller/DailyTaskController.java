package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.DailyTaskDto;
import lap_english.dto.TaskDto;
import lap_english.dto.UserDailyTaskDto;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IDailyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/daily-task")
@RequiredArgsConstructor
public class DailyTaskController {
    private final IDailyTaskService dailyTaskService;
//    private final ITaskService taskService;


    @Operation(summary = "nhận phần thưởng của nhiệm vụ đã hoàn thành ", description = "Trả về nhiệm vụ đã nhận ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, Trả về subtopic đã mở khóa"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/claim-reward/{dailyTaskId}")
    public ResponseData<UserDailyTaskDto> create(@PathVariable Long dailyTaskId) {
      return  new ResponseData<>(HttpStatus.OK.value(), "success", dailyTaskService.claimReward(dailyTaskId));
    }

    @Operation(summary = "Tạo mới", description = "Trả về đối tượng vừa tạo ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping
    public ResponseData<DailyTaskDto> create(@Validated @RequestBody TaskDto taskDto) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "success", dailyTaskService.create(taskDto));
    }

    @Operation(summary = "xoa task", description = "k tra ve gi ca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @DeleteMapping("/{id}")
    public ResponseData<String> delete(@PathVariable Long id) {
        dailyTaskService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "success", "Xóa thành công");
    }

    @Operation(summary = "update", description = "Trả về đối tượng vừa update ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<DailyTaskDto> update(@PathVariable Long id, @Validated @RequestBody TaskDto taskDto) {
        taskDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", dailyTaskService.update(taskDto));
    }
    @Operation(summary = "getPage daily task ", description = "Trả về danh sach daily task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa tạo "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<PageResponse<List<DailyTaskDto>>> getPage(Pageable pageable, @RequestParam(required = false) String[] tasks) {
        return new ResponseData<>(HttpStatus.OK.value(), "success", dailyTaskService.getAllTask(pageable, tasks));
    }
}

