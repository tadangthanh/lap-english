package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SubTopicDto extends BaseDto {
    @NotBlank(message = "Name is required", groups = {Create.class, Update.class})
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String blobName;
    @NotBlank(message = "mainTopicName is required")
    private String mainTopicName;
    @NotNull(message = "mainTopicId is required", groups = {Create.class})
    private Long mainTopicId;
    private int wordCount;
    private boolean isLearned;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date lastLearnDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date completedDate;
    private boolean word;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int diamond;
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int gold;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LockStatusManager status;
}
