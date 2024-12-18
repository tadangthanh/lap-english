package lap_english.service;

import lap_english.dto.TaskDto;
import lap_english.dto.TitleDto;
import lap_english.dto.UserTitleDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface ITitleService {
    void claimTitle(Long titleId);
    TitleDto create(TitleDto titleDto, MultipartFile file);
    void delete(Long id);
    TitleDto update(TitleDto titleDto,MultipartFile file);
    PageResponse<List<TitleDto>> getAllTask(Pageable pageable);
}
