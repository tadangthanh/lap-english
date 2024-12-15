package lap_english.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccumulateDto {
    private int words;
    private int daysLearned;
    private int sentences;
    private int titles;
}
