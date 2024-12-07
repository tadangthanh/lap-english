package lap_english.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lap_english.dto.request.AuthRequest;
import lap_english.dto.response.ResponseData;
import lap_english.dto.response.TokenResponse;
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
    @PostMapping("/access")
    public ResponseData<TokenResponse> login(@Validated @RequestBody AuthRequest authRequest) {
        return new ResponseData<>(HttpStatus.OK.value(), "Login success", authenticationService.login(authRequest));
    }

    @PostMapping({"/refresh"})
    public ResponseData<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        return new ResponseData<>(HttpStatus.OK.value(), "refresh success", authenticationService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public String logout() {
        return "logout sucess";
    }
//    @Operation(summary = "Truy cập để lấy token ", description = "Add new user to database")
//    @PostMapping({"/verify-token"})
//    public ResponseData<?> verifyToken(@RequestHeader("Access-Token") String token) {
//        this.authenticationService.verifyToken(token);
//        return new ResponseData<>(HttpStatus.OK.value(), "Token is valid", null);
//    }
}

