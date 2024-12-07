package lap_english.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainTopicDto extends BaseDto {
    @NotBlank(message = "Name is required")
    private String name;
    private boolean word;
}
