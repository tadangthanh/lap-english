package lap_english.mapper;

import lap_english.dto.SkillDto;
import lap_english.entity.Skill;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)

public interface SkillMapper {
    SkillDto toDto(Skill skill);
    Skill toEntity(SkillDto skillDto);
}
