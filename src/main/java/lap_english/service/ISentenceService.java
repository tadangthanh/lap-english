package lap_english.service;

import lap_english.dto.SentenceDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface ISentenceService {
    void delete(Long id);

    SentenceDto save(SentenceDto sentenceDto);

    PageResponse<?> getAll(int page, int size);

    SentenceDto findById(Long id);

    SentenceDto update(SentenceDto sentenceDto);

    PageResponse<?> getBySubTopicId(Long subTopicId, int page, int size);

    PageResponse<?> advancedSearch(Pageable pageable, String[] sentence);
    CompletableFuture<Integer> importFromExcel(Long subTopicId, MultipartFile file);

}
