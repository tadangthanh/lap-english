package lap_english.dto;

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
public class GrammaticalStructureDto extends BaseDto {
    @NotBlank(message = "description is required", groups = {Create.class, Update.class})
    private String description;
    @NotBlank(message = "structure is required", groups = {Create.class, Update.class})
    private String structure;
    @NotNull(message = "grammarId is required", groups = {Create.class})
    private Long grammarId;
}
