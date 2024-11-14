package lap_english.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentenceDto extends BaseDto {
    @NotBlank(message = "Sentence is required", groups = {Create.class, Update.class})
    private String sentence;
    @NotBlank(message = "Translation is required", groups = {Create.class, Update.class})
    private String translation;
    @NotNull(message = "Sub topic id is required", groups = Create.class)
    private Long subTopicId;
    private String subTopicName;
}
