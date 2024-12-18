package lap_english.service;

import lap_english.dto.DailyTaskDto;
import lap_english.dto.TaskDto;
import lap_english.dto.UserDailyTaskDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDailyTaskService {
    void claimReward(Long dailyTaskId);
    DailyTaskDto create(TaskDto taskDto);
    void delete(Long id);
    DailyTaskDto update(TaskDto taskDto);
    PageResponse<List<DailyTaskDto>> getAllTask(Pageable pageable);
}
