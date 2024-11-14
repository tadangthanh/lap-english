package lap_english.mapper;

import lap_english.dto.SentenceDto;
import lap_english.entity.Sentence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface SentenceMapper {
    @Mapping(target = "subTopicId", source = "subTopic.id")
    @Mapping(target = "subTopicName", source = "subTopic.name")
    SentenceDto toDto(Sentence entity);

    Sentence toEntity(SentenceDto dto);

    List<SentenceDto> toListDto(List<Sentence> entities);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(SentenceDto dto, @MappingTarget Sentence entity);
}
