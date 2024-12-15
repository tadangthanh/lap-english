package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.*;
import lap_english.dto.response.UserResponseDto;
import lap_english.entity.*;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.*;
import lap_english.repository.UserDailyTaskRepo;
import lap_english.repository.UserRepo;
import lap_english.repository.UserTitleRepo;
import lap_english.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AccumulateMapper accumulateMapper;
    private final CumulativePointMapper cumulativePointMapper;
    private final UserDailyTaskRepo userDailyTaskRepo;
    private final RewardMapper rewardMapper;
    private final UserTitleRepo userTitleRepo;
    private final TitleMapper titleMapper;
    private final TaskMapper taskMapper;

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

    @Override
    public UserResponseDto getUserDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        User currentUser = getCurrentUser();
        userResponseDto.setId(currentUser.getId());
        userResponseDto.setEmail(currentUser.getEmail());
        userResponseDto.setName(currentUser.getName());
        Accumulate accumulate = currentUser.getAccumulate();
        // set accumulate
        if (accumulate != null) {
            AccumulateDto accumulateDto = accumulateMapper.toDto(accumulate);
            userResponseDto.setAccumulate(accumulateDto);
        }
        // set cumulate point
        CumulativePoint cumulativePoint = currentUser.getCumulativePoint();
        if (cumulativePoint != null) {
            CumulativePointDto pointDto = cumulativePointMapper.toDto(cumulativePoint);
            userResponseDto.setCumulativePoint(pointDto);
        }
        List<UserDailyTask> userDailyTasks = userDailyTaskRepo.findAllByUserId(currentUser.getId());
        List<DailyTaskDto> dailyTaskDtoList = new ArrayList<>();
        // set dailytask dto
        userDailyTasks.forEach(userDailyTask -> {
            DailyTaskDto dailyTaskDto = new DailyTaskDto();
            DailyTask dailyTask = userDailyTask.getDailyTask();
            if (dailyTask != null) {
                // set task va reward cho daily task dto
                RewardDto rewardDto = rewardMapper.toDto(dailyTask.getReward());
                rewardDto.setRewardClaimed(userDailyTask.isRewardClaimed());
                dailyTaskDto.setReward(rewardDto);
                TaskDto taskDto = taskMapper.toDto(dailyTask.getTask());
                taskDto.setProgress(userDailyTask.getProgress());
                dailyTaskDto.setTask(taskDto);
                dailyTaskDtoList.add(dailyTaskDto);
            }
        });
        // set titles
        List<UserTitle> userTitles = userTitleRepo.findAllByUserId(currentUser.getId());
        List<TitleDto> titleDtoList = new ArrayList<>();
        userTitles.forEach(userTitle -> {
            TitleDto titleDto = titleMapper.toDto(userTitle.getTitle());
            RewardDto rewardDto = rewardMapper.toDto(userTitle.getTitle().getReward());
            rewardDto.setRewardClaimed(userTitle.isRewardClaimed());
            titleDto.setReward(rewardDto);
            TaskDto taskDto = taskMapper.toDto(userTitle.getTitle().getTask());
            taskDto.setProgress(userTitle.getProgress());
            titleDto.setTask(taskDto);
            titleDtoList.add(titleDto);
        });

        userResponseDto.setDailyTasks(dailyTaskDtoList);
        userResponseDto.setTitles(titleDtoList);

        return userResponseDto;
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
