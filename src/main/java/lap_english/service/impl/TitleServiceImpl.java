package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.RewardDto;
import lap_english.dto.TaskDto;
import lap_english.dto.TitleDto;
import lap_english.dto.UserTitleDto;
import lap_english.dto.response.PageResponse;
import lap_english.entity.*;
import lap_english.exception.BadRequestException;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.RewardMapper;
import lap_english.mapper.TaskMapper;
import lap_english.mapper.TitleMapper;
import lap_english.mapper.UserTitleMapper;
import lap_english.repository.*;
import lap_english.service.IAzureService;
import lap_english.service.ITitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TitleServiceImpl implements ITitleService {
    private final UserRepo userRepo;
    private final UserTitleRepo userTitleRepo;
    private final TitleRepo titleRepo;
    private final UserTitleMapper userTitleMapper;
    private final CumulativePointRepo cumulativePointRepo;
    private final TitleMapper titleMapper;
    private final RewardMapper rewardMapper;
    private final RewardRepo rewardRepo;
    private final TaskMapper taskMapper;
    private final TaskRepo taskRepo;
    private final IAzureService azureService;

    @Override
    public UserTitleDto claimTitle(Long titleId) {
        User user = getCurrentUser();
        UserTitle userTitle = findUserTitleByTitleIdAndUserIdAndDateNowOrThrow(titleId, user.getId());
        // neu phan thuong nhan thuong roi thi thoi
        if (userTitle.isRewardClaimed()) {
            log.error("Reward already claimed for title Id: {}", titleId);
            throw new BadRequestException("Reward already claimed");
        }
        // neu progress chua 100% thi thoi
        Title title = userTitle.getTitle();
        Task task = title.getTask();
        double total = task.getTotal();
        if (userTitle.getProgress() < total) {
            log.error("Progress is not 100% for title id: {}", titleId);
            throw new BadRequestException("Progress is not 100%");
        }
        // lay phan thuong

        userTitle.setRewardClaimed(true);
        userTitleRepo.save(userTitle);
        // cong tien thuong cho user
        Reward reward = title.getReward();
        addRewardToUser(user, reward);
        // tra ve usertitleDto
        UserTitleDto userTitleDto = userTitleMapper.toDto(userTitle);
        userTitleDto.setUserId(user.getId());
        TitleDto titleDto = titleMapper.toDto(title);
        RewardDto rewardDto = rewardMapper.toDto(reward);
        rewardDto.setRewardClaimed(true);
        TaskDto taskDto = taskMapper.toDto(task);
        titleDto.setTask(taskDto);
        titleDto.setReward(rewardDto);
        userTitleDto.setTitle(titleDto);
        userTitleDto.setRewardClaimed(true);
        return userTitleDto;
    }

    @Override
    public TitleDto create(TitleDto titleDto, MultipartFile file) {
        // set title
        Title title = titleMapper.toEntity(titleDto);
        if (file != null) {
            String imageUrl = azureService.upload(file);
            title.setImage(imageUrl);
        }
        title = titleRepo.save(title);
        // set reward
        Reward reward = rewardMapper.toEntity(titleDto.getReward());
        reward = rewardRepo.save(reward);
        title.setReward(reward);
        // set task
        Task task = taskMapper.toEntity(titleDto.getTask());
        task = taskRepo.save(task);
        title.setTask(task);
        title = titleRepo.save(title);
        return convertTitleToDto(title);
    }

    private TitleDto convertTitleToDto(Title title) {
        TitleDto titleDto = titleMapper.toDto(title);
        RewardDto rewardDto = rewardMapper.toDto(title.getReward());
        TaskDto taskDto = taskMapper.toDto(title.getTask());
        titleDto.setReward(rewardDto);
        titleDto.setTask(taskDto);
        return titleDto;
    }

    @Override
    public void delete(Long id) {
        Title title = findTitleByIdOrThrow(id);
        String blobName = title.getImage();
        userTitleRepo.deleteAllByTitleId(title.getId());
        titleRepo.delete(title);
        deleteFile(blobName);
    }

    @Override
    public TitleDto update(TitleDto titleDto, MultipartFile file) {
        Title titleExist = findTitleByIdOrThrow(titleDto.getId());
        // Lưu đường dẫn file cần xóa
        String blobName = titleExist.getImage();
        titleMapper.updateFromDto(titleDto, titleExist);
        titleExist = titleRepo.save(titleExist);
        // Đăng ký hành động xóa file sau khi transaction commit thành công
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                if (file != null) {
                    deleteFile(blobName);
                }
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    // Thực hiện hành động khác nếu transaction không thành công
                    log.error("Transaction rolled back, no file deletion.");
                }
            }
        });
        uploadImage(file, titleExist);
        return convertTitleToDto(titleExist);
    }

    private void uploadImage(MultipartFile file, Title title) {
        if (file == null || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return;
        }
        title.setImage(azureService.upload(file));
    }

    void deleteFile(String blobName) {
        if (blobName != null && !blobName.isEmpty()) {
            azureService.deleteBlob(blobName);
        }
    }


    @Override
    public PageResponse<List<TitleDto>> getAllTask(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Title> titlePage = titleRepo.findAll(pageRequest);
        List<TitleDto> titleDtos = titlePage.getContent().stream()
                .map(this::convertTitleToDto)
                .toList();
        return PageResponse.<List<TitleDto>>builder().items(titleDtos).totalItems(titlePage.getTotalElements()).totalPage(titlePage.getTotalPages()).hasNext(titlePage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }


    private Title findTitleByIdOrThrow(Long titleId) {
        return titleRepo.findById(titleId)
                .orElseThrow(() -> {
                    log.error("Title not found with id: {}", titleId);
                    return new ResourceNotFoundException("Title not found");
                });
    }

    private void addRewardToUser(User user, Reward reward) {
        CumulativePoint cumulativePoint = findCumulativePointByUserIdOrThrow(user.getId());
        cumulativePoint.setDiamond(cumulativePoint.getDiamond() + reward.getDiamond());
        cumulativePoint.setGold(cumulativePoint.getGold() + reward.getGold());
        cumulativePointRepo.save(cumulativePoint);
    }

    private CumulativePoint findCumulativePointByUserIdOrThrow(Long userId) {
        return cumulativePointRepo.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("CumulativePoint not found with userId: {}", userId);
                    return new ResourceNotFoundException("CumulativePoint not found");
                });
    }

    private UserTitle findUserTitleByTitleIdAndUserIdAndDateNowOrThrow(Long titleId, Long userId) {
        Date now = new Date();
        return userTitleRepo.findByTitleIdAndUserId(titleId, userId, now)
                .orElseThrow(() -> {
                    log.error("UserTitle not found");
                    return new ResourceNotFoundException("UserTitle not found");
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
}
