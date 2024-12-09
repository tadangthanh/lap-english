package lap_english.service;

import lap_english.dto.request.AuthRequest;
import lap_english.dto.request.LoginGoogleRequest;
import lap_english.dto.response.TokenResponse;

public interface IAuthenticationService {
    TokenResponse login(AuthRequest authRequest);

    TokenResponse loginWithGoogle(LoginGoogleRequest loginGoogleRequest);

    void logout(String token);

    void verifyToken(String token);

    TokenResponse refreshToken(String refreshToken);
}
