package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainTopicDto extends BaseDto {
    @NotBlank(message = "Name is required")
    private String name;
    private boolean word;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int diamound;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int gold;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LockStatusManager status;
}
