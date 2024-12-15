package lap_english.dto.response;

import lap_english.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto extends BaseDto {
    private String email;
    private String name;
    private String avatar;
    private SkillDto skill;
    private List<TitleDto> titles;
    private List<DailyTaskDto> dailyTasks;
    private CumulativePointDto cumulativePoint;
    private AccumulateDto accumulate;
}
