package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.DailyTaskDto;
import lap_english.dto.RewardDto;
import lap_english.dto.TaskDto;
import lap_english.dto.UserDailyTaskDto;
import lap_english.entity.*;
import lap_english.exception.BadRequestException;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.DailyTaskMapper;
import lap_english.mapper.RewardMapper;
import lap_english.mapper.TaskMapper;
import lap_english.mapper.UserDailyTaskMapper;
import lap_english.repository.*;
import lap_english.service.IDailyTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DailyTaskServiceImpl implements IDailyTaskService {
    private final TaskRepo taskRepo;
    private final UserDailyTaskRepo userDailyTaskRepo;
    private final DailyTaskRepo dailyTaskRepo;
    private final UserRepo userRepo;
    private final UserDailyTaskMapper userDailyTaskMapper;
    private final CumulativePointRepo cumulativePointRepo;
    private final DailyTaskMapper dailyTaskMapper;
    private final TaskMapper taskMapper;
    private final RewardMapper rewardMapper;

    @Override
    public UserDailyTaskDto claimReward(Long dailyTaskId) {
        User user = getCurrentUser();
        UserDailyTask userDailyTask = findUserDailyTaskByDailyTaskAndUserIdOrThrow(dailyTaskId, user.getId());
        // neu phan thuong nhan thuong roi thi thoi
        if (userDailyTask.isRewardClaimed()) {
            log.error("Reward already claimed for dailyTaskId: {}", dailyTaskId);
            throw new BadRequestException("Reward already claimed");
        }
        // neu progress chua 100% thi thoi
        DailyTask dailyTask = userDailyTask.getDailyTask();
        Task task = dailyTask.getTask();
        double total = task.getTotal();
        if (userDailyTask.getProgress() < total) {
            log.error("Progress is not 100% for dailyTaskId: {}", dailyTaskId);
            throw new BadRequestException("Progress is not 100%");
        }
        // lay phan thuong
        // cap nhat lai phan thuong
        userDailyTask.setRewardClaimed(true);
        userDailyTaskRepo.save(userDailyTask);
        // cong tien thuong cho user
        Reward reward = dailyTask.getReward();
        addRewardToUser(user, reward);
        UserDailyTaskDto userDailyTaskDto = userDailyTaskMapper.toDto(userDailyTask);
        userDailyTaskDto.setUserId(user.getId());
        DailyTaskDto dailyTaskDto = dailyTaskMapper.toDto(dailyTask);
        RewardDto rewardDto = rewardMapper.toDto(reward);
        dailyTaskDto.setReward(rewardDto);
        TaskDto taskDto = taskMapper.toDto(task);
        dailyTaskDto.setTask(taskDto);
        userDailyTaskDto.setDailyTask(dailyTaskDto);
        return userDailyTaskDto;
    }

    private void addRewardToUser(User user, Reward reward) {
        CumulativePoint cumulativePoint = findCumulativePointByUserIdOrThrow(user.getId());
        cumulativePoint.setDiamond(cumulativePoint.getDiamond() + reward.getDiamond());
        cumulativePoint.setGold(cumulativePoint.getGold() + reward.getGold());
        cumulativePointRepo.save(cumulativePoint);
    }

    private UserDailyTask findUserDailyTaskByDailyTaskAndUserIdOrThrow(Long dailyTaskId, Long userId) {
        return userDailyTaskRepo.findByDailyTaskAndUserId(dailyTaskId,userId)
                .orElseThrow(() -> {
                    log.error("UserDailyTask not found with dailyTaskId: {}", dailyTaskId);
                    return new RuntimeException("UserDailyTask not found");
                });
    }

    private CumulativePoint findCumulativePointByUserIdOrThrow(Long userId) {
        return cumulativePointRepo.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("CumulativePoint not found with userId: {}", userId);
                    return new ResourceNotFoundException("CumulativePoint not found");
                });
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found");
                    return new ResourceNotFoundException("User not found");
                });
    }
}
