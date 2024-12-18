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
@Table(name = "accumulate")
public class Accumulate extends BaseEntity {
    private int words;
    private int daysLearned;
    private int sentences;
    private int titles;
}
