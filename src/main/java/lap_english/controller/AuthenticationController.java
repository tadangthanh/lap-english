package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lap_english.dto.request.AuthRequest;
import lap_english.dto.request.LoginGoogleRequest;
import lap_english.dto.response.ResponseData;
import lap_english.dto.response.TokenResponse;
import lap_english.exception.ErrorObjectDetails;
import lap_english.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication controller", description = "APIs for authentication")
public class AuthenticationController {
    private final IAuthenticationService authenticationService;

    @Operation(summary = "Login", description = "nhap tai khoan va mat khau de lay token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dang nhap thanh cong, tra ve token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "403", description = "Sai tai khoan hoac mat khau",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Không có quyền truy cập",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/access")
    public ResponseData<TokenResponse> login(@Validated @RequestBody AuthRequest authRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login success", authenticationService.login(authRequest));
    }

    @Operation(summary = "login with google", description = "dang nhap bang tai khoan google")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dang nhap thanh cong, tra ve token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request: Lỗi validation dữ liệu truyền vào",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "403", description = "Sai tai khoan hoac mat khau",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Không có quyền truy cập",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorObjectDetails.class))),
            @ApiResponse(responseCode = "404", description = "không tìm thấy đối tượng liên quan"),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ.")})
    @PostMapping("/login-google")
    public ResponseData<TokenResponse> loginWithGoogle(@Validated @RequestBody LoginGoogleRequest loginGoogleRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login success", authenticationService.loginWithGoogle(loginGoogleRequest));
    }

    @PostMapping({"/refresh"})
    public ResponseData<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return new ResponseData<>(HttpStatus.OK.value(), "refresh success", authenticationService.refreshToken(refreshToken));
    }
    @PostMapping({"/verify-token"})
    public ResponseData<?> verifyToken(@RequestHeader("Access-Token") String token) {
        this.authenticationService.verifyToken(token);
        return new ResponseData<>(HttpStatus.OK.value(), "Token is valid", null);
    }
}




