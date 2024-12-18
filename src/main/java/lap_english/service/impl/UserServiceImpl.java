package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.*;
import lap_english.dto.response.UserResponseDto;
import lap_english.entity.*;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.*;
import lap_english.repository.*;
import lap_english.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
    private final SkillMapper skillMapper;
    private final TaskMapper taskMapper;
    private final CumulativePointRepo cumulativePointRepo;
    private final DailyTaskRepo dailyTaskRepo;
    private final DailyTaskMapper dailyTaskMapper;

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

    private boolean isDailyTaskOld() {
        boolean isOld = false;
        User user = getCurrentUser();
        List<UserDailyTask> userDailyTasks = userDailyTaskRepo.findAllByUserId(user.getId());
        for(UserDailyTask u : userDailyTasks) {
            // neu la nhiem vu cua ngay cu thi xoa di
            Date createdAt = u.getCreatedAt();
            LocalDate taskCreatedDate = createdAt.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate currentDate = LocalDate.now();
            if (!taskCreatedDate.isEqual(currentDate)) {
                isOld = true;
                break;
            }
        }
        return isOld;
    }

    @Override
    public UserResponseDto getUserDto() {
        User user = getCurrentUser();

        // moi khi userlogin thi kiem tra xem nhiem vu hang ngay cua user xem co phai la nhiem vu cua ngay cu hayk
        // neu la nhiem vu cua ngay cu thi xoa di va tao lai nhiem vu moi
        // tạo lại nhiệm vụ hàng ngày cho user
        if(isDailyTaskOld()) {
            generateDailyTask(user);
        }

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getName());
        Accumulate accumulate = user.getAccumulate();
        // set accumulate
        if (accumulate != null) {
            AccumulateDto accumulateDto = accumulateMapper.toDto(accumulate);
            userResponseDto.setAccumulate(accumulateDto);
        }
        // set cumulate point
        CumulativePoint cumulativePoint = cumulativePointRepo.findByUserId(user.getId()).orElse(null);
        if (cumulativePoint != null) {
            CumulativePointDto pointDto = cumulativePointMapper.toDto(cumulativePoint);
            userResponseDto.setCumulativePoint(pointDto);
        }
        List<UserDailyTask>userDailyTasks = userDailyTaskRepo.findAllByUserId(user.getId());
        List<DailyTaskDto> dailyTaskDtoList = new ArrayList<>();
        // set dailytask dto
        userDailyTasks.forEach(userDailyTask -> {
            DailyTaskDto dailyTaskDto = dailyTaskMapper.toDto(userDailyTask.getDailyTask());
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
        List<UserTitle> userTitles = userTitleRepo.findAllByUserId(user.getId());
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
        userResponseDto.setSkills(skillMapper.toDto(user.getSkill()));
        userResponseDto.setDailyTasks(dailyTaskDtoList);
        userResponseDto.setTitles(titleDtoList);
        userResponseDto.setAvatar(user.getAvatar());
        return userResponseDto;
    }

    private void generateDailyTask(User user) {
        // danh sach cac task cua ngay cu
        List<UserDailyTask> userDailyTasksOld = userDailyTaskRepo.findAllByUserId(user.getId());
        List<Long> idsOldDailyTask = userDailyTasksOld.stream().map(userDailyTask -> userDailyTask.getDailyTask().getId()).toList();
        // lay cac task moi 1 cach ngau nhien
        int dailyTaskNum = 3;
        Pageable pageable = PageRequest.of(0, dailyTaskNum);
        List<DailyTask> taskRandom = dailyTaskRepo.findRandomDailyTasks(idsOldDailyTask, pageable);
//        List<UserDailyTask> userDailyTasksNew = new ArrayList<>();
        for (DailyTask dailyTask : taskRandom) {
            // init userDaily Task
            UserDailyTask userDailyTask = new UserDailyTask();
            userDailyTask.setUser(user);
            userDailyTask.setDailyTask(dailyTask);
            userDailyTask.setProgress(0);
            userDailyTask.setRewardClaimed(false);
            userDailyTask = userDailyTaskRepo.saveAndFlush(userDailyTask);
//            userDailyTasksNew.add(userDailyTask);
        }
        // xoa cac task cu
        idsOldDailyTask.forEach(userDailyTaskRepo::deleteAllByDailyTaskId);
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
