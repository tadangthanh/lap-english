package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.TaskDto;
import lap_english.dto.TaskFor;
import lap_english.dto.response.PageResponse;
import lap_english.entity.DailyTask;
import lap_english.entity.Reward;
import lap_english.entity.Task;
import lap_english.entity.Title;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.RewardMapper;
import lap_english.mapper.TaskMapper;
import lap_english.repository.*;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.ITaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements ITaskService {
    private final TaskMapper taskMapper;
    private final RewardRepo rewardRepo;
    private final RewardMapper rewardMapper;
    private final TaskRepo taskRepo;
    private final DailyTaskRepo dailyTaskRepo;
    private final TitleRepo titleRepo;
    private final UserDailyTaskRepo userDailyTaskRepo;
    private final UserTitleRepo userTitleRepo;

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        Reward reward = rewardMapper.toEntity(taskDto.getReward());
        reward = rewardRepo.saveAndFlush(reward);
        task.setReward(reward);
        task = taskRepo.saveAndFlush(task);
        if (taskDto.getTaskFor().equals(TaskFor.DAILY)) {
            DailyTask dailyTask = new DailyTask();
            dailyTask.setTask(task);
            dailyTask.setReward(reward);
            dailyTask = dailyTaskRepo.saveAndFlush(dailyTask);
        } else if (taskDto.getTaskFor().equals(TaskFor.TITLE)) {
            Title title = new Title();
            title.setTask(task);
            title.setReward(reward);
            title = titleRepo.saveAndFlush(title);
        }
        return convertTaskToDto(task);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = findTaskByIdOrThrow(id);
        DailyTask dailyTask = dailyTaskRepo.findByTaskId(id).orElse(null);
        Title title = titleRepo.findByTaskId(id).orElse(null);
        if (dailyTask != null) {
            userDailyTaskRepo.deleteAllByDailyTaskId(dailyTask.getId());
            dailyTaskRepo.delete(dailyTask);
        }
        if (title != null) {
            userTitleRepo.deleteAllByTitleId(title.getId());
            titleRepo.delete(title);
        }
        taskRepo.delete(task);
    }

    @Override
    public TaskDto update(TaskDto taskDto) {
        Task taskExist = findTaskByIdOrThrow(taskDto.getId());
        taskMapper.updateFromDto(taskDto, taskExist);
        taskExist = taskRepo.saveAndFlush(taskExist);
        return convertTaskToDto(taskExist);
    }

    @Override
    public PageResponse<List<TaskDto>> getAllTask(Pageable pageable, String[] tasks) {
        if (tasks != null && tasks.length > 0) {
            EntitySpecificationsBuilder<Task> builder = new EntitySpecificationsBuilder<Task>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"); //?page=0&size=10&sort=id,desc&subtopic=name~d
            for (String s : tasks) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Page<Task> taskPage = taskRepo.findAll(builder.build(), pageable);
            return convertToPageResponse(taskPage, pageable);
        }
        return convertToPageResponse(taskRepo.findAll(pageable), pageable);
    }

    private PageResponse<List<TaskDto>> convertToPageResponse(Page<Task> taskPage, Pageable pageable) {
        List<TaskDto> response = taskPage.stream().map(this::convertTaskToDto).collect(toList());
        return PageResponse.<List<TaskDto>>builder().items(response).totalItems(taskPage.getTotalElements()).totalPage(taskPage.getTotalPages()).hasNext(taskPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    private List<TaskDto> convertTaskToDto(List<Task> tasks) {
        return tasks.stream().map(this::convertTaskToDto).collect(toList());
    }

    private Task findTaskByIdOrThrow(Long id) {
        return taskRepo.findById(id).orElseThrow(() -> {
            log.warn("Task not found");
            return new ResourceNotFoundException("Task not found");
        });
    }

    private TaskDto convertTaskToDto(Task task) {
        TaskDto taskDto = taskMapper.toDto(task);
        Reward reward = task.getReward();
        taskDto.setReward(rewardMapper.toDto(reward));
        taskDto.setTaskFor(dailyTaskRepo.existByTaskId(task.getId()) ? TaskFor.DAILY : TaskFor.TITLE);
        return taskDto;
    }
}
