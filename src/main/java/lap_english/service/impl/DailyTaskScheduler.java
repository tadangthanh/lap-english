package lap_english.service.impl;

import lap_english.dto.*;
import lap_english.dto.response.UserResponseDto;
import lap_english.entity.*;
import lap_english.mapper.*;
import lap_english.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyTaskScheduler {
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
    private final TaskRepo taskRepo;
    private final DailyTaskRepo dailyTaskRepo;

    //    // Sự kiện sẽ chạy vào 00:00:00 mỗi ngày
    @Scheduled(cron = "0 0 0 * * ?")
// Chạy vào 16:37 mỗi ngày
    public void runAtMidnight() {
        List<User> users =userRepo.findAll();
        for (User user : users) {
            generateDailyTask(user);
        }
    }
    private  void generateDailyTask(User user) {
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
            userDailyTask=userDailyTaskRepo.saveAndFlush(userDailyTask);
//            userDailyTasksNew.add(userDailyTask);
        }
        // xoa cac task cu
        idsOldDailyTask.forEach(userDailyTaskRepo::deleteAllByDailyTaskId);
    }

}
