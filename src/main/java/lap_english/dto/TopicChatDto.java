package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicChatDto extends BaseDto {
    @NotBlank(message = "Name is required",groups = {Create.class, Update.class})
    private String name;
    @NotBlank(message = "description is required",groups = {Create.class, Update.class})
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String blobName;
    @NotNull(message = "User is required",groups = {Create.class})
    private Long userId;
}
