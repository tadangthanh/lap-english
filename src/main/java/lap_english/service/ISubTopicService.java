package lap_english.service;

import lap_english.dto.SubTopicDto;
import lap_english.dto.response.PageResponse;

public interface ISubTopicService {
    SubTopicDto create(SubTopicDto subTopicDto);

    void delete(Long id);

    SubTopicDto update(SubTopicDto subTopicDto);

    PageResponse<?> getAll(int page, int size);

    PageResponse<?> getByMainTopicId(Long mainTopicId, int page, int size);
}
