package lap_english.mapper;

import lap_english.dto.WordDto;
import lap_english.entity.Word;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface WordMapper {
    @Mapping(target = "subTopicName", source = "subTopic.name")
    @Mapping(target = "subTopicId", source = "subTopic.id")
    WordDto toDto(Word entity);

    Word toEntity(WordDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(WordDto dto, @MappingTarget Word entity);

    List<WordDto> toListDto(List<Word> content);
}
