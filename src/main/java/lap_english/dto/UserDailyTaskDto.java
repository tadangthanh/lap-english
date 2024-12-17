package lap_english.dto;

import lap_english.entity.DailyTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDailyTaskDto extends BaseDto{
    private DailyTaskDto dailyTask;
    private Long userId;
    private double progress;
    private boolean isRewardClaimed;
}
