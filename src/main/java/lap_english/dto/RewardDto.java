package lap_english.dto;

import lap_english.entity.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RewardDto {
    private RewardType rewardType;
    private int quantity;
}
