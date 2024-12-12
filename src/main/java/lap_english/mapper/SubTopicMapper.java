package lap_english.mapper;

import lap_english.dto.SubTopicDto;
import lap_english.entity.SubTopic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface SubTopicMapper {
    @Mapping(target = "mainTopicName", source = "mainTopic.name")
    @Mapping(target = "mainTopicId", source = "mainTopic.id")
    @Mapping(target = "blobName", source = "blobName")
    @Mapping(target = "status.diamond", source = "diamond")
    @Mapping(target = "status.gold", source = "gold")
    SubTopicDto toDto(SubTopic entity);

    //    @Mapping(target = "isWord", source = "isWord")
//    @Mapping(target = "isSentence", source = "isSentence")
    @Mapping(target = "diamond", source = "diamond")
    @Mapping(target = "gold", source = "gold")
    SubTopic toEntity(SubTopicDto dto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diamond", source = "diamond")
    @Mapping(target = "gold", source = "gold")
    void updateEntityFromDto(SubTopicDto dto, @MappingTarget SubTopic entity);

    List<SubTopicDto> toListDto(List<SubTopic> content);


}
