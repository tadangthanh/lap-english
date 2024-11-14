package lap_english.service;

import lap_english.dto.MainTopicDto;
import lap_english.dto.response.PageResponse;

import java.util.List;

public interface IMainTopicService {
    MainTopicDto create(MainTopicDto mainTopicDto);

    void delete(Long id);

    MainTopicDto update(MainTopicDto mainTopicDto);

    PageResponse<?> getPage(int page, int size);
    List<MainTopicDto> getAll();
}
