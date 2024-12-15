package lap_english.mapper;

import lap_english.dto.RewardDto;
import lap_english.entity.Reward;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)

public interface RewardMapper {
    RewardDto toDto(Reward reward);
    Reward toEntity(RewardDto rewardDto);
}
