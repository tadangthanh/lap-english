package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.entity.FunTaskQuiz;
import lap_english.entity.TypeTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto extends BaseDto {
    @NotBlank(message = "description is required")
    private String description;
    private TypeTask type;
    private FunTaskQuiz keyFunUpdate;
    @NotNull(message = "total is required")
    private Double total;
    private Double progress;
    @NotNull(message = "reward is required")
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private RewardDto reward;
//    @NotNull(message = "taskFor is required")
//    private TaskFor taskFor;
}
