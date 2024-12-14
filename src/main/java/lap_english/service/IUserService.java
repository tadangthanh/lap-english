package lap_english.service;

import lap_english.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
    UserDto update(UserDto userDto);
    String getUerJson();
}
