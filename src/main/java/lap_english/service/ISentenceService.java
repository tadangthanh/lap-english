package lap_english.service;

import lap_english.dto.SentenceDto;
import lap_english.dto.response.PageResponse;

public interface ISentenceService {
    void delete(Long id);

    SentenceDto save(SentenceDto sentenceDto);

    PageResponse<?> getAll(int page, int size);

    SentenceDto findById(Long id);

    SentenceDto update(SentenceDto sentenceDto);

    PageResponse<?> getBySubTopicId(Long subTopicId, int page, int size);
}
