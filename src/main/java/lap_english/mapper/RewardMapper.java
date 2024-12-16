package lap_english.mapper;

import lap_english.dto.RewardDto;
import lap_english.entity.Reward;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)

public interface RewardMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "gold", source = "gold")
    @Mapping(target = "diamond", source = "diamond")
    RewardDto toDto(Reward reward);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "gold", source = "gold")
    @Mapping(target = "diamond", source = "diamond")
    Reward toEntity(RewardDto rewardDto);
}
