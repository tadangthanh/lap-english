package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reward")// qua
public class Reward extends BaseEntity{
    private RewardType rewardType;
    private int quantity;
    private boolean isRewardClaimed;
}
