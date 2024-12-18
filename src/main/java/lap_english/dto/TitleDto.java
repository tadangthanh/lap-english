package lap_english.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TitleDto extends  BaseDto{
    private String image;
    private String title;
    private TaskDto task;
    private RewardDto reward;
}
