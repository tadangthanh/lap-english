package lap_english.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTitleDto extends BaseDto{
    private TitleDto title;
    private Long userId;
    private double progress;
    private boolean isRewardClaimed;
}
