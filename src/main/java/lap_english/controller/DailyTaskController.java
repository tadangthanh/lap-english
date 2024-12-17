package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.UserDailyTaskDto;
import lap_english.dto.response.ResponseData;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IDailyTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/daily-task")
@RequiredArgsConstructor
public class DailyTaskController {
    private final IDailyTaskService dailyTaskService;


    @Operation(summary = "nhận phần thưởng của nhiệm vụ hàng ngày  đã hoàn thành ", description = "không trả về dữ liệu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, không trả về dữ liệu"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi do progress chưa đạt 100% hoặc đã nhận thưởng rồi ",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/claim-reward/{dailyTaskId}")
    public ResponseData<Void> create(@PathVariable Long dailyTaskId) {
        dailyTaskService.claimReward(dailyTaskId);
      return  new ResponseData<>(HttpStatus.OK.value(), "success", null);
    }
}
