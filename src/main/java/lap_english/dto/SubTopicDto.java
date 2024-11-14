package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.validation.Create;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class SubTopicDto extends BaseDto {
    @NotBlank(message = "Name is required", groups = {Create.class})
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imageUrl;
    @NotBlank(message = "Name is required")
    private String mainTopicName;
    @NotNull(message = "Name is required", groups = {Create.class})
    private Long mainTopicId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MultipartFile file;
}
