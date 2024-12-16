package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lap_english.entity.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RewardDto extends BaseDto{
    private int gold;
    private int diamond;
    @JsonProperty("isRewardClaimed")
    private boolean isRewardClaimed;
}
