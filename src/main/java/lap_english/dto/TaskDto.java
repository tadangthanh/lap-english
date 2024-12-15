package lap_english.dto;

import lap_english.entity.TypeTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private String description;
    private TypeTask type;
    private String keyFunUpdate;
    private double total;
    private double progress;
}
