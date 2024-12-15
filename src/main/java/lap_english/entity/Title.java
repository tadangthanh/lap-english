package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "title") // danh hieu
public class Title extends BaseEntity {
    private String image;
    private String title;
    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;

}
