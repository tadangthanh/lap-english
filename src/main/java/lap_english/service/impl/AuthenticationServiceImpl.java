package lap_english.service.impl;

import lap_english.dto.request.AuthRequest;
import lap_english.dto.response.TokenResponse;
import lap_english.entity.User;
import lap_english.exception.ResourceNotFoundException;
import lap_english.repository.UserRepo;
import lap_english.service.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            User user = userRepo.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Tài khoản hoặc mật khẩu không đúng"));
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException("Tài khoản hoặc mật khẩu không đúng");
        }
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public void verifyToken(String token) {
        if (!jwtService.tokenIsValid(token)) {
            throw new ResourceNotFoundException("Token không hợp lệ");
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        String username = this.jwtService.extractUsername(refreshToken);
        if (this.jwtService.tokenIsValid(refreshToken)) {
            User user = this.userRepo.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            String newAccessToken = this.jwtService.generateToken(user);
            String newRefreshToken = this.jwtService.generateRefreshToken(user);
            return TokenResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
        } else {
            throw new ResourceNotFoundException("Invalid refresh token");
        }
    }
}
