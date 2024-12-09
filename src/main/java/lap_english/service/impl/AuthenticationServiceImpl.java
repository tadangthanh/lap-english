package lap_english.service.impl;

import lap_english.dto.request.AuthRequest;
import lap_english.dto.request.LoginGoogleRequest;
import lap_english.dto.response.TokenResponse;
import lap_english.entity.Role;
import lap_english.entity.User;
import lap_english.exception.ResourceNotFoundException;
import lap_english.repository.RoleRepo;
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
    private final RoleRepo roleRepo;

    @Override
    public TokenResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            User user = userRepo.findByEmail(authRequest.getEmail())
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
    public TokenResponse loginWithGoogle(LoginGoogleRequest loginGoogleRequest) {
        User user = userRepo.findByEmail(loginGoogleRequest.getEmail()).orElse(null);
        if (user == null) {
            user = saveUser(loginGoogleRequest);
        }
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .json(user.getJson())
                .build();
    }

    private User saveUser(LoginGoogleRequest loginGoogleRequest) {
        User user = new User();
        user.setEmail(loginGoogleRequest.getEmail());
        user.setUsername(loginGoogleRequest.getEmail());
        user.setName(loginGoogleRequest.getName());
        user.setJson(loginGoogleRequest.getJson());
        Role role = roleRepo.findRoleByName("ROLE_USER").orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.setRole(role);
        user = userRepo.saveAndFlush(user);
        return userRepo.saveAndFlush(user);
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
        String email = this.jwtService.extractUsername(refreshToken);
        if (this.jwtService.tokenIsValid(refreshToken)) {
            User user = this.userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            String newAccessToken = this.jwtService.generateToken(user);
            String newRefreshToken = this.jwtService.generateRefreshToken(user);
            return TokenResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
        } else {
            throw new ResourceNotFoundException("Invalid refresh token");
        }
    }
}
