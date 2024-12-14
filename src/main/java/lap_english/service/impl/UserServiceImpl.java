package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.UserDto;
import lap_english.entity.User;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.UserMapper;
import lap_english.repository.UserRepo;
import lap_english.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDto update(UserDto userDto) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userDto.getId())) {
            throw new ResourceNotFoundException("User not found");
        }
        User userExist = findUserByIdOrThrow(userDto.getId());
        userMapper.updateFromDto(userDto, userExist);
        userRepo.save(userExist);
        return userMapper.toDto(userExist);
    }

    @Override
    public String getUerJson() {
        User currentUser = getCurrentUser();
        return currentUser.getJson();
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepo.findById(id).orElseThrow(() -> {
            log.warn("User not found with id: {}", id);
            return new ResourceNotFoundException("User not found");
        });
    }

    private User getCurrentUser() {
        return (User) loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
