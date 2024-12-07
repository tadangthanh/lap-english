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
public class GrammarDto extends BaseDto {
    @NotBlank(message = "name is required", groups = {Create.class, Update.class})
    private String name;
    @NotBlank(message = "description is required", groups = {Create.class, Update.class})
    private String description;
    @NotNull(message = "typeGrammarId is required", groups = {Create.class})
    private Long typeGrammarId;
}
