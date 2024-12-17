package lap_english.service;

import lap_english.dto.UserDailyTaskDto;

public interface IDailyTaskService {
    UserDailyTaskDto claimReward(Long dailyTaskId);
}
