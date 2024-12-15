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
@Table(name = "cumulative_point") //Điểm tích lũy
public class CumulativePoint extends BaseEntity {
    //Điểm
    private int diamond;
    private int gold;
    //Điểm đua top
    private int rankPoints;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
