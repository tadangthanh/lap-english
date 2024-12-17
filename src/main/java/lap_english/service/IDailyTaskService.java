package lap_english.service;

import lap_english.dto.DailyTaskDto;
import lap_english.dto.UserDailyTaskDto;
import lap_english.entity.UserDailyTask;

public interface IDailyTaskService {
    UserDailyTaskDto claimReward(Long dailyTaskId);
}
