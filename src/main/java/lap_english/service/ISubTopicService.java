package lap_english.service;

import lap_english.dto.SubTopicDto;
import lap_english.dto.request.QuizResult;
import lap_english.dto.response.PageResponse;
import lap_english.entity.SubTopic;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISubTopicService {
    SubTopicDto create(SubTopicDto subTopicDto, MultipartFile file);

    void delete(Long id);

    SubTopicDto update(SubTopicDto subTopicDto, MultipartFile file);

    PageResponse<?> getPage(int page, int size, String sort);

    PageResponse<List<SubTopicDto>> getByMainTopicId(Long mainTopicId, int page, int size);

    PageResponse<List<SubTopicDto>> findByName(String name, int page, int size, String sort);

    PageResponse<List<SubTopicDto>> advanceSearchBySpecification(Pageable pageable, String[] subTopic);

    void deleteByMainTopicId(Long mainTopicId);

    SubTopicDto getById(Long id);

    SubTopicDto complete(Long id);

    boolean unlock(Long id);

    void updateQuiz(QuizResult quizResult);
}
