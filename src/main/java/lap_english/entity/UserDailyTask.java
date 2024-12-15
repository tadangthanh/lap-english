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
@Table(name = "user_daily_task")
public class UserDailyTask extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "daily_task_id")
    private DailyTask dailyTask;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
