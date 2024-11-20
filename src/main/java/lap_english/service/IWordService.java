package lap_english.service;

import lap_english.dto.WordDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface IWordService {
    void delete(Long id);

    WordDto create(WordDto dto);

    WordDto update(WordDto dto);

    PageResponse<?> getBySubTopicId(Long subTopicId, Integer page, Integer size);

    PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] word);

    void deleteBySubTopicId(Long subTopicId);

    CompletableFuture<Integer> importFromExcel(Long subTopicId, MultipartFile file);

}
