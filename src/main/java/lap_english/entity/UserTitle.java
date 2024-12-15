package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "user_title")
public class UserTitle extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "title_id")
    private Title title;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private double progress;
    private boolean isRewardClaimed;
}
