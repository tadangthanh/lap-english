package lap_english.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task") // nhiem vu
public class Task extends BaseEntity{
    private String description;
    @Enumerated(EnumType.STRING)
    private TypeTask type;
    @Enumerated(EnumType.STRING)
    private FunTaskQuiz keyFunUpdate;
    private double total; // tổng số nhiệm vụ cần hoàn thành
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reward_id")
    private Reward reward;
}
