package lap_english.mapper;

import lap_english.dto.MainTopicDto;
import lap_english.entity.MainTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface MainTopicMapper {

    @Mapping(target = "status.diamond", source = "diamond")
    @Mapping(target = "status.gold", source = "gold")
    MainTopicDto toDto(MainTopic entity);


    MainTopic toEntity(MainTopicDto dto);


    List<MainTopicDto> toListDto(List<MainTopic> content);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subTopics", ignore = true)
    @Mapping(target = "word", ignore = true)
    void updateEntityFromDto(MainTopicDto dto,@MappingTarget MainTopic entity);
}
