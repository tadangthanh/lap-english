package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lap_english.dto.UserDto;
import lap_english.dto.response.ResponseData;
import lap_english.dto.response.UserResponseDto;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
@Validated
public class UserController {
    private final IUserService userService;

    @Operation(summary = "Update user theo  id", description = "Trả về đối tượng vừa update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PutMapping("/{id}")
    public ResponseData<UserDto> update(@PathVariable Long id, @Validated @RequestBody UserDto userDto) {
        userDto.setId(id);
        return new ResponseData<>(HttpStatus.OK.value(), "success", userService.update(userDto));
    }

    @Operation(summary = "lay json cua user", description = "Trả ve chuoi json ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng  vừa update "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping("/json")
    public ResponseData<String> json() {
        return new ResponseData<>(HttpStatus.OK.value(), "success", userService.getUerJson());
    }

    @Operation(summary = "lấy user hiện tại ", description = "Trả về đối tượng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công, trả về đối tượng "),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @GetMapping
    public ResponseData<UserResponseDto> getAuthenticationUser() {
        return new ResponseData<>(HttpStatus.OK.value(), "success", userService.getUserDto());
    }
}
