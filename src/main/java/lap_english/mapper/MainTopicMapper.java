package lap_english.mapper;

import lap_english.dto.MainTopicDto;
import lap_english.entity.MainTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface MainTopicMapper {

    MainTopicDto toDto(MainTopic entity);


    MainTopic toEntity(MainTopicDto dto);


    List<MainTopicDto> toListDto(List<MainTopic> content);
}
