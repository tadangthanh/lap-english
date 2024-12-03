package lap_english.mapper;

import lap_english.dto.TopicChatDto;
import lap_english.entity.TopicChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface TopicChatMapper {

    @Mapping(target = "userId", source = "user.id")
    TopicChatDto toDto(TopicChat entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    TopicChat toEntity(TopicChatDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(TopicChatDto dto, @MappingTarget TopicChat entity);
}
