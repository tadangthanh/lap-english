package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.DailyTaskDto;
import lap_english.dto.RewardDto;
import lap_english.dto.TaskDto;
import lap_english.dto.UserDailyTaskDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.*;
import lap_english.exception.BadRequestException;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.DailyTaskMapper;
import lap_english.mapper.RewardMapper;
import lap_english.mapper.TaskMapper;
import lap_english.mapper.UserDailyTaskMapper;
import lap_english.repository.*;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IDailyTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DailyTaskServiceImpl implements IDailyTaskService {
    private final TaskRepo taskRepo;
    private final UserDailyTaskRepo userDailyTaskRepo;
    private final DailyTaskRepo dailyTaskRepo;
    private final UserRepo userRepo;
    private final UserDailyTaskMapper userDailyTaskMapper;
    private final CumulativePointRepo cumulativePointRepo;
    private final DailyTaskMapper dailyTaskMapper;
    private final TaskMapper taskMapper;
    private final RewardMapper rewardMapper;
    private final RewardRepo rewardRepo;
    private final TitleRepo titleRepo;
    private final UserTitleRepo userTitleRepo;

    @Override
    public void claimReward(Long dailyTaskId) {
        User user = getCurrentUser();
        // nhiệm vụ của ngày hiện tại
        UserDailyTask userDailyTask = findUserDailyTaskByDailyTaskAndUserIdAndDateNowOrThrow(dailyTaskId, user.getId());
        // neu phan thuong nhan thuong roi thi thoi
        if (userDailyTask.isRewardClaimed()) {
            log.error("Reward already claimed for dailyTaskId: {}", dailyTaskId);
            throw new BadRequestException("Reward already claimed");
        }
        // neu progress chua 100% thi thoi
        DailyTask dailyTask = userDailyTask.getDailyTask();
        Task task = dailyTask.getTask();
        double total = task.getTotal();
        if (userDailyTask.getProgress() < total) {
            log.error("Progress is not 100% for dailyTaskId: {}", dailyTaskId);
            throw new BadRequestException("Progress is not 100%");
        }
        // lay phan thuong
        // cap nhat lai phan thuong
        userDailyTask.setRewardClaimed(true);
        userDailyTaskRepo.save(userDailyTask);
        // cong tien thuong cho user
        Reward reward = dailyTask.getReward();
        addRewardToUser(user, reward);
        // tra ve thong tin userDailyTask da nhan thuong
//        UserDailyTaskDto userDailyTaskDto = userDailyTaskMapper.toDto(userDailyTask);
//        userDailyTaskDto.setUserId(user.getId());
//        DailyTaskDto dailyTaskDto = dailyTaskMapper.toDto(dailyTask);
//        RewardDto rewardDto = rewardMapper.toDto(reward);
//        rewardDto.setRewardClaimed(true);
//        dailyTaskDto.setReward(rewardDto);
//        TaskDto taskDto = taskMapper.toDto(task);
//        dailyTaskDto.setTask(taskDto);
//        userDailyTaskDto.setDailyTask(dailyTaskDto);
//        return userDailyTaskDto;
    }

    private void addRewardToUser(User user, Reward reward) {
        CumulativePoint cumulativePoint = findCumulativePointByUserIdOrThrow(user.getId());
        cumulativePoint.setDiamond(cumulativePoint.getDiamond() + reward.getDiamond());
        cumulativePoint.setGold(cumulativePoint.getGold() + reward.getGold());
        cumulativePointRepo.save(cumulativePoint);
    }

    // lấy ra nhiệm vụ hàng ngày của user hiện tại dựa trên dailyTaskId và userId và ngày hiện tại
    private UserDailyTask findUserDailyTaskByDailyTaskAndUserIdAndDateNowOrThrow(Long dailyTaskId, Long userId) {
        Date now = new Date();
        return userDailyTaskRepo.findByDailyTaskAndUserId(dailyTaskId, userId, now)
                .orElseThrow(() -> {
                    log.error("UserDailyTask not found with dailyTaskId: {}", dailyTaskId);
                    return new RuntimeException("UserDailyTask not found");
                });
    }

    private CumulativePoint findCumulativePointByUserIdOrThrow(Long userId) {
        return cumulativePointRepo.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("CumulativePoint not found with userId: {}", userId);
                    return new ResourceNotFoundException("CumulativePoint not found");
                });
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found");
                    return new ResourceNotFoundException("User not found");
                });
    }
    @Override
    public DailyTaskDto create(TaskDto taskDto) {
        Task task = taskMapper.toEntity(taskDto);
        Reward reward = rewardMapper.toEntity(taskDto.getReward());
        reward = rewardRepo.saveAndFlush(reward);
        task.setReward(reward);
        task = taskRepo.saveAndFlush(task);
//        if (taskDto.getTaskFor().equals(TaskFor.DAILY)) {
//            DailyTask dailyTask = new DailyTask();
//            dailyTask.setTask(task);
//            dailyTask.setReward(reward);
//            dailyTask = dailyTaskRepo.saveAndFlush(dailyTask);
//        } else if (taskDto.getTaskFor().equals(TaskFor.TITLE)) {
//            Title title = new Title();
//            title.setTask(task);
//            title.setReward(reward);
//            title = titleRepo.saveAndFlush(title);
//        }
        DailyTask dailyTask = new DailyTask();
        dailyTask.setTask(task);
        dailyTask.setReward(reward);
        dailyTaskRepo.save(dailyTask);
//        TaskDto taskResult = convertTaskToDto(task);
//        RewardDto rewardDto = rewardMapper.toDto(reward);
//        DailyTaskDto dailyTaskDto = dailyTaskMapper.toDto(dailyTask);
//        dailyTaskDto.setTask(taskResult);
//        dailyTaskDto.setReward(rewardDto);
//        return dailyTaskDto;
        return convertTaskToDailyTaskDto(task);
    }

    @Override
    public void delete(Long id) {
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
    public DailyTaskDto update(TaskDto taskDto) {
        Task taskExist = findTaskByIdOrThrow(taskDto.getId());
        taskMapper.updateFromDto(taskDto, taskExist);
        taskExist = taskRepo.saveAndFlush(taskExist);
        DailyTask dailyTask = dailyTaskRepo.findByTaskId(taskDto.getId()).orElseThrow(()->{
            log.error("DailyTask not found");
            return new ResourceNotFoundException("DailyTask not found");
        });
//        TaskDto taskResult= convertTaskToDto(taskExist);
//        DailyTaskDto dailyTaskDto = dailyTaskMapper.toDto(dailyTask);
//        dailyTaskDto.setTask(taskDto);
        Reward reward = rewardMapper.toEntity(taskDto.getReward());
        reward = rewardRepo.saveAndFlush(reward);
//        RewardDto rewardDto = rewardMapper.toDto(reward);
//        dailyTaskDto.setReward(rewardDto);
//        dailyTaskDto.setTask(taskResult);

        return convertTaskToDailyTaskDto(taskExist);
    }

    @Override
    public PageResponse<List<DailyTaskDto>> getAllTask(Pageable pageable) {
//        if (tasks != null && tasks.length > 0) {
//            EntitySpecificationsBuilder<Task> builder = new EntitySpecificationsBuilder<Task>();
//            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"); //?page=0&size=10&sort=id,desc&subtopic=name~d
//            for (String s : tasks) {
//                Matcher matcher = pattern.matcher(s);
//                if (matcher.find()) {
//                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
//                }
//            }
//            Page<Task> taskPage = taskRepo.findAll(builder.build(), pageable);
//            return convertToPageResponse(taskPage, pageable);
//        }
        List<Long> ids = dailyTaskRepo.findAll().stream().map(DailyTask::getTask).map(Task::getId).toList();
        return convertToPageResponse(taskRepo.findAllByIdIn(ids,pageable), pageable);
    }

    private PageResponse<List<DailyTaskDto>> convertToPageResponse(Page<Task> taskPage, Pageable pageable) {
        List<DailyTaskDto> response = taskPage.stream().map(this::convertTaskToDailyTaskDto).collect(toList());
        return PageResponse.<List<DailyTaskDto>>builder().items(response).totalItems(taskPage.getTotalElements()).totalPage(taskPage.getTotalPages()).hasNext(taskPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }
    private DailyTaskDto convertTaskToDailyTaskDto(Task task) {
        DailyTask dailyTask = dailyTaskRepo.findByTaskId(task.getId()).orElseThrow(() -> {
            log.error("DailyTask not found");
            return new ResourceNotFoundException("DailyTask not found");
        });
        DailyTaskDto dailyTaskDto = dailyTaskMapper.toDto(dailyTask);
        TaskDto taskDto = taskMapper.toDto(task);
        Reward reward = task.getReward();
        RewardDto rewardDto = rewardMapper.toDto(reward);
        dailyTaskDto.setTask(taskDto);
        dailyTaskDto.setReward(rewardDto);
        return dailyTaskDto;
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
//        taskDto.setTaskFor(dailyTaskRepo.existByTaskId(task.getId()) ? TaskFor.DAILY : TaskFor.TITLE);
        return taskDto;
    }
}
