package lap_english.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lap_english.entity.WordLevel;
import lap_english.entity.WordType;
import lap_english.validation.Create;
import lap_english.validation.Update;
import lap_english.validation.ValidEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class WordDto extends BaseDto {
    @NotBlank(message = "Word is required", groups = {Create.class, Update.class})
    private String word;
    @NotBlank(message = "meaning is required", groups = {Create.class, Update.class})
    private String meaning;
    @NotBlank(message = "pronounceUK is required", groups = {Create.class, Update.class})
    private String pronounceUK;
    @NotBlank(message = "pronounceUS is required", groups = {Create.class, Update.class})
    private String pronounceUS;
    @ValidEnum(name = "word type", regexp = "NOUN|VERB|ADJECTIVE|ADVERB|PREPOSITION|CONJUNCTION|INTERJECTION|PRONOUN|DETERMINER|EXCLAMATION", groups = {Create.class, Update.class})
    private WordType type;
    @ValidEnum(name = "word level", regexp = "A1|A2|B1|B2|C1|C2", groups = {Create.class, Update.class})
    private WordLevel level;
    @NotBlank(message = "example is required", groups = {Create.class, Update.class})
    private String example;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subTopicName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "subTopicId is required", groups = Create.class)
    private Long subTopicId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String audioUkBlobName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String audioUsBlobName;
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imageBlobName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MultipartFile file;


}
