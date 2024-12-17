package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.UserTitleDto;
import lap_english.entity.*;
import lap_english.exception.BadRequestException;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.TitleMapper;
import lap_english.mapper.UserTitleMapper;
import lap_english.repository.CumulativePointRepo;
import lap_english.repository.TitleRepo;
import lap_english.repository.UserRepo;
import lap_english.repository.UserTitleRepo;
import lap_english.service.ITitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    @Override
    public void claimTitle(Long titleId) {
        User user = getCurrentUser();
        UserTitle userTitle = findUserTitleByTitleIdAndUserIdOrThrow(titleId, user.getId());
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
        Reward reward = title.getReward();
        addRewardToUser(user, reward);
        // xoa userTitle
        userTitleRepo.delete(userTitle);
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
    private UserTitle findUserTitleByTitleIdAndUserIdOrThrow(Long titleId, Long userId) {
        return userTitleRepo.findByTitleIdAndUserId(titleId, userId)
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
