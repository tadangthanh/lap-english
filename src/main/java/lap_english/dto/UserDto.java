package lap_english.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto extends BaseDto {
    private String username;
    private String email;
    private String avatar;
    private String name;
}
