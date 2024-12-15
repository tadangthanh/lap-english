package lap_english.dto;

import lap_english.entity.SkillType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillDto {
    private double reading;
    private double writing;
    private double speaking;
    private double listening;
    private SkillType type;
}
