package lap_english.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginGoogleRequest {
    @NotBlank(message = "email is required")
    private String email;
    @NotBlank(message = "name is required")
    private String name;
    private String json;
}
