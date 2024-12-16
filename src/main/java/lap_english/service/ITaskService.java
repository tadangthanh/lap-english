package lap_english.service;

import lap_english.dto.TaskDto;
import lap_english.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaskService {
    TaskDto createTask(TaskDto taskDto);
    void deleteTask(Long id);
    TaskDto update(TaskDto taskDto);
    PageResponse<List<TaskDto>> getAllTask(Pageable pageable,String[] tasks);
}
