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
    private String keyFunUpdate;
    private double total;
    @OneToOne
    @JoinColumn(name = "reward_id")
    private Reward reward;
}
