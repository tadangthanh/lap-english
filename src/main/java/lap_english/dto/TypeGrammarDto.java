package lap_english.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeGrammarDto extends BaseDto {
    @NotBlank(message = "Name is required")
    private String name;
}
