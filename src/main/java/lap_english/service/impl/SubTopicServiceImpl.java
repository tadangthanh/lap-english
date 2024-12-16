package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.LockStatusManager;
import lap_english.dto.SubTopicDto;
import lap_english.dto.request.QuizResult;
import lap_english.dto.request.TypeQuizResult;
import lap_english.dto.response.PageResponse;
import lap_english.entity.*;
import lap_english.exception.DuplicateResource;
import lap_english.exception.ResourceInUseException;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.SubTopicMapper;
import lap_english.repository.*;
import lap_english.repository.specification.EntitySpecificationsBuilder;
import lap_english.service.IAzureService;
import lap_english.service.ISubTopicService;
import lap_english.service.IWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubTopicServiceImpl implements ISubTopicService {
    private final SubTopicMapper subTopicMapper;
    private final SubTopicRepo subTopicRepo;
    private final MainTopicRepo mainTopicRepo;
    private final IAzureService azureService;
    private final WordRepo wordRepo;
    private final IWordService wordService;
    private final UserLearnedSubTopicRepo userLearnedSubTopicRepo;
    private final UserRepo userRepo;
    private final UserSubTopicRepo userSubTopicRepo;
    private final CumulativePointRepo cumulativePointRepo;
    private final AccumulateRepo accumulateRepo;
    private final SkillRepo skillRepo;
    private final UserDailyTaskRepo userDailyTaskRepo;
    private final TaskRepo taskRepo;
    private final TitleRepo titleRepo;
    private final DailyTaskRepo dailyTaskRepo;
    private final UserTitleRepo userTitleRepo;


    @Override
    public SubTopicDto create(SubTopicDto subTopicDto, MultipartFile file) {
        validateSubTopic(subTopicDto);
        MainTopic mainTopic = getMainTopicById(subTopicDto.getMainTopicId());
        SubTopic subTopic = subTopicMapper.toEntity(subTopicDto);
        subTopic.setMainTopic(mainTopic);
        subTopic = subTopicRepo.save(subTopic);
        uploadImage(file, subTopic);
        return convertSubtopicToDto(subTopic);
    }

    private void validateSubTopic(SubTopicDto subTopicDto) {
        if (this.subTopicRepo.existsByNameAndMainTopicId(subTopicDto.getName(), subTopicDto.getMainTopicId())) {
            throw new DuplicateResource("Sub Topic already exists");
        }
    }

    private void uploadImage(MultipartFile file, SubTopic subTopic) {
        if (file == null || !Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return;
        }
        subTopic.setBlobName(azureService.upload(file));
    }


    @Override
    public void delete(Long id) {
        if (isSubtopicIsUsed(id)) {
            log.error("Sub Topic is used");
            throw new ResourceInUseException("Sub Topic is used, cannot delete");
        }
        if (userLearnedSubTopicRepo.existsBySubTopicId(id)) {
            log.error("Sub Topic is learned");
            throw new ResourceInUseException("Sub Topic is learned by user, cannot delete");
        }
        SubTopic subTopic = subTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));
        String blobName = subTopic.getBlobName();
        subTopicRepo.delete(subTopic);
        wordService.deleteBySubTopicId(id);
        // Đăng ký hành động xóa file sau khi transaction commit thành công
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteFile(blobName);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    // Thực hiện hành động khác nếu transaction không thành công
                    log.error("Transaction rolled back, no file deletion.");
                }
            }
        });
    }

    private boolean isSubtopicIsUsed(Long id) {
        return userSubTopicRepo.existsBySubTopicId(id);
    }

    @Override
    public SubTopicDto update(SubTopicDto subTopicDto, MultipartFile file) {
        SubTopic subTopicExist = subTopicRepo.findById(subTopicDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));

        // Lưu đường dẫn file cần xóa
        String blobName = subTopicExist.getBlobName();

        subTopicMapper.updateEntityFromDto(subTopicDto, subTopicExist);
        subTopicExist = subTopicRepo.save(subTopicExist);
        if (subTopicDto.getMainTopicId() != null && !subTopicExist.getMainTopic().getId().equals(subTopicDto.getMainTopicId())) {
            MainTopic mainTopic = getMainTopicById(subTopicDto.getMainTopicId());
            subTopicExist.setMainTopic(mainTopic);
        }
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

        uploadImage(file, subTopicExist);
        // Sau khi lưu thành công, trả về DTO
        return convertSubtopicToDto(subTopicExist);
    }

    void deleteFile(String blobName) {
        if (blobName != null && !blobName.isEmpty()) {
            azureService.deleteBlob(blobName);
        }
    }

    @Override
    public PageResponse<?> getPage(int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("name").ascending() : Sort.by("name").descending());
        Page<SubTopic> subTopicPage = subTopicRepo.findAll(pageRequest);
        List<SubTopicDto> subTopicDtos = convertSubtopicToDto(subTopicPage.getContent());
        return PageResponse.builder().items(subTopicDtos).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<List<SubTopicDto>> getByMainTopicId(Long mainTopicId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SubTopic> subTopicPage = subTopicRepo.findByMainTopicId(mainTopicId, pageRequest);
        List<SubTopicDto> subTopicDtos = convertSubtopicToDto(subTopicPage.getContent());
        return PageResponse.<List<SubTopicDto>>builder().items(subTopicDtos).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<List<SubTopicDto>> findByName(String name, int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, sort.equals("asc") ? Sort.by("name").ascending() : Sort.by("name").descending());
        Page<SubTopic> subTopicPage = subTopicRepo.findByNameContaining(name, pageRequest);
        List<SubTopicDto> subTopicDtos = convertSubtopicToDto(subTopicPage.getContent());
        return PageResponse.<List<SubTopicDto>>builder().items(subTopicDtos).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(page).pageSize(size).build();
    }

    @Override
    public PageResponse<List<SubTopicDto>> advanceSearchBySpecification(Pageable pageable, String[] subTopic) {
        log.info("request get all of sub topic with specification");
        if (subTopic != null && subTopic.length > 0) {
            EntitySpecificationsBuilder<SubTopic> builder = new EntitySpecificationsBuilder<SubTopic>();
            Pattern pattern = Pattern.compile("(\\w+?)([<:>~!])(.*)(\\p{Punct}?)(\\p{Punct}?)"); //?page=0&size=10&sort=id,desc&subtopic=name~d
            for (String s : subTopic) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            Page<SubTopic> subTopicPage = subTopicRepo.findAll(builder.build(), pageable);
            return convertToPageResponse(subTopicPage, pageable);
        }
        return convertToPageResponse(subTopicRepo.findAll(pageable), pageable);
    }

    @Override
    public void deleteByMainTopicId(Long mainTopicId) {
        List<SubTopic> subTopics = subTopicRepo.findAllByMainTopicId(mainTopicId);
        for (SubTopic subTopic : subTopics) {
            wordService.deleteBySubTopicId(subTopic.getId());
            subTopicRepo.delete(subTopic);
        }
    }

    @Override
    public SubTopicDto getById(Long id) {
        SubTopic subTopic = subTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sub Topic not found"));
        return convertSubtopicToDto(subTopic);
    }

    @Override
    public SubTopicDto complete(Long id) {
        SubTopic subTopicExist = findSubtopicByIdOrThrow(id);
        User currentUser = getCurrentUser();
        UserLearnedSubTopic userLearnedSubTopic = userLearnedSubTopicRepo.findByUserIdAndSubTopicId(currentUser.getId(), subTopicExist.getId()).orElse(null);
        if (userLearnedSubTopic == null) {
            userLearnedSubTopic = new UserLearnedSubTopic();
            userLearnedSubTopic.setSubTopic(subTopicExist);
            userLearnedSubTopic.setUser(currentUser);
            userLearnedSubTopic.setCompletedDate(new Date());
            userLearnedSubTopicRepo.saveAndFlush(userLearnedSubTopic);
            return convertSubtopicToDto(subTopicExist);
        }
        userLearnedSubTopic.setCompletedDate(new Date());
        userLearnedSubTopicRepo.saveAndFlush(userLearnedSubTopic);
        return convertSubtopicToDto(subTopicExist);
    }

    @Override
    public boolean unlock(Long id) {
        User user = getCurrentUser();
        SubTopic subTopic = findSubtopicByIdOrThrow(id);
        boolean isUnlock = userSubTopicRepo.existsByUserIdAndSubTopicId(user.getId(), subTopic.getId());
        if (isUnlock) {
            return true;
        }
        CumulativePoint cumulativePoint = findCumulativePointByUserIdOrThrow(user.getId());
        if (cumulativePoint.getGold() >= subTopic.getGold() && cumulativePoint.getDiamond() >= subTopic.getDiamond()) {
            cumulativePoint.setGold(cumulativePoint.getGold() - subTopic.getGold());
            cumulativePoint.setDiamond(cumulativePoint.getDiamond() - subTopic.getDiamond());
            cumulativePointRepo.save(cumulativePoint);
            UserSubTopic userSubTopic = new UserSubTopic();
            userSubTopic.setSubTopic(subTopic);
            userSubTopic.setUser(user);
            userSubTopicRepo.saveAndFlush(userSubTopic);
            return true;
        }

        return false;
    }

    @Override
    public void updateQuiz(QuizResult quizResult) {
        User currentUser = getCurrentUser();
        boolean isLearned = userLearnedSubTopicRepo.existsByUserIdAndSubtopicId(currentUser.getId(), quizResult.getIdObject());
        if(!isLearned){
            SubTopic subTopicExist = findSubtopicByIdOrThrow(quizResult.getIdObject());
            UserLearnedSubTopic userLearnedSubTopic = new UserLearnedSubTopic();
            userLearnedSubTopic.setSubTopic(subTopicExist);
            userLearnedSubTopic.setUser(currentUser);
            userLearnedSubTopic.setCompletedDate(new Date());
            userLearnedSubTopicRepo.saveAndFlush(userLearnedSubTopic);
        }
        quizResult.setLearned(isLearned);
        //--- Cập nhật nhiệm vụ quiz  ---
        List<UserDailyTask> userDailyTasks = userDailyTaskRepo.findAllByUserId(currentUser.getId());
        userDailyTasks.forEach(userDailyTask -> {
            funUpDateTaskQuiz(userDailyTask, quizResult);
        });
        //--- Cập nhật kĩ năng  ---
        Skill skill = currentUser.getSkill();
        updateSkillByUser(skill, quizResult);
        skillRepo.saveAndFlush(skill);

        //--- Cập nhật điểm (vàng)  ---
        CumulativePoint cumulativePoint = findCumulativePointByUserIdOrThrow(currentUser.getId());
        cumulativePoint.setGold(cumulativePoint.getGold() + quizResult.getBonus());
        cumulativePoint.setRankPoints(cumulativePoint.getRankPoints() + quizResult.getPointRank());
        cumulativePointRepo.saveAndFlush(cumulativePoint);

        //--- Cập nhật điểm tích lũy  ---
        Accumulate accumulate = currentUser.getAccumulate();
        if(!quizResult.isLearned()){
            accumulate.setWords(accumulate.getWords() + quizResult.getTotalWord());
            accumulate.setSentences(accumulate.getSentences() + quizResult.getTotalSentence());
        }
        accumulateRepo.saveAndFlush(accumulate);
    }

    private void updateSkillByUser(Skill skill, QuizResult quizResult) {
        double totalSkill = skill.getListening() + skill.getReading() + skill.getWriting() + skill.getSpeaking();
        if (quizResult.getTotalRead() > 0) {
            skill.setReading(skill.getReading() + (double) quizResult.getCorrectRead() / quizResult.getTotalRead() * (1 - skill.getReading() / totalSkill));
        }
        if (quizResult.getTotalListen() > 0) {
            skill.setListening(skill.getListening() + (double) quizResult.getCorrectListen() / quizResult.getTotalListen() * (1 - skill.getListening() / totalSkill));
        }
        if (quizResult.getTotalSpeak() > 0) {
            skill.setSpeaking(skill.getSpeaking() + (double) quizResult.getCorrectSpeak() / quizResult.getTotalSpeak() * (1 - skill.getSpeaking() / totalSkill));
        }
        if (quizResult.getTotalWrite() > 0) {
            skill.setWriting(skill.getWriting() + (double) quizResult.getCorrectWrite() / quizResult.getTotalWrite() * (1 - skill.getWriting() / totalSkill));
        }
    }

    private void funUpDateTaskQuiz(UserDailyTask userDailyTask, QuizResult quizResult) {
        Task task = userDailyTask.getDailyTask().getTask();
        try {
            FunTaskQuiz funTaskQuiz = FunTaskQuiz.valueOf(task.getKeyFunUpdate());
            switch (funTaskQuiz) {
                case funLearnNewTopicWord:
                    if (!quizResult.isLearned()) {
                        userDailyTask.setProgress(Math.min(Math.max(userDailyTask.getProgress() + 1, 0), task.getTotal()));
                    }
                    break;
                case funLearnReviewTopicWord:
                    if (quizResult.isLearned()) {
                        userDailyTask.setProgress(Math.min(Math.max(userDailyTask.getProgress() + 1, 0), task.getTotal()));
                    }
                    break;
                case funLearnNewTopicWord80:
                    if (!quizResult.isLearned() && ((double) quizResult.getCorrect() / quizResult.getTotal()) > -0.8) {
                        userDailyTask.setProgress(Math.min(Math.max(userDailyTask.getProgress() + 1, 0), task.getTotal()));
                    }
                    break;
                case funLearnReviewTopicWord90:
                    if (quizResult.isLearned() && ((double) quizResult.getCorrect() / quizResult.getTotal()) > -0.9) {
                        userDailyTask.setProgress(Math.min(Math.max(userDailyTask.getProgress() + 1, 0), task.getTotal()));
                    }
                    break;
                case funLearnWithSkillWrite100:
                    if (quizResult.getCorrectWrite() == quizResult.getTotalWrite()) {
                        userDailyTask.setProgress(Math.min(Math.max(userDailyTask.getProgress() + 1, 0), task.getTotal()));
                    }
                    break;
                case funLearnReviewVocabulary:
                    if (quizResult.isLearned() && quizResult.getType() == TypeQuizResult.quizzVocabulary) {
                        userDailyTask.setProgress(Math.min(Math.max(userDailyTask.getProgress() + 1, 0), task.getTotal()));
                    }
                    break;
                default:
                    log.warn("Unrecognized quiz result: {}", quizResult);
            }
        } catch (Exception e) {
            log.error("Error updating quiz result", e);
        }
    }

    private CumulativePoint findCumulativePointByUserIdOrThrow(Long id) {
        return cumulativePointRepo.findByUserId(id).orElseThrow(() -> {
            log.warn("cumulative point not found for user {}", id);
            return new ResourceNotFoundException("cumulative point not found for user " + id);
        });
    }

    private PageResponse<List<SubTopicDto>> convertToPageResponse(Page<SubTopic> subTopicPage, Pageable pageable) {
        List<SubTopicDto> response = subTopicPage.stream().map(this::convertSubtopicToDto).collect(toList());
        response.forEach(subTopicDto -> {
            subTopicDto.setWordCount(wordRepo.countBySubTopicId(subTopicDto.getId()));
        });
        return PageResponse.<List<SubTopicDto>>builder().items(response).totalItems(subTopicPage.getTotalElements()).totalPage(subTopicPage.getTotalPages()).hasNext(subTopicPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    private SubTopicDto convertSubtopicToDto(SubTopic subTopic) {
        SubTopicDto subTopicDto = subTopicMapper.toDto(subTopic);
        subTopicDto.setWordCount(wordRepo.countBySubTopicId(subTopicDto.getId()));
        User currentUser = getCurrentUser();
        UserLearnedSubTopic userLearnedSubTopic = userLearnedSubTopicRepo.findByUserIdAndSubTopicId(currentUser.getId(), subTopic.getId()).orElse(null);
        LockStatusManager status = new LockStatusManager();
        status.setDiamond(subTopic.getDiamond());
        status.setGold(subTopic.getGold());
        User user = getCurrentUser();
        status.setLocked(!userSubTopicRepo.existsByUserIdAndSubTopicId(user.getId(), subTopic.getId()));
        subTopicDto.setStatus(status);
        if (userLearnedSubTopic == null) {
            return subTopicDto;
        }
        subTopicDto.setCompletedDate(userLearnedSubTopic.getCompletedDate());
        subTopicDto.setLearned(true);


        return subTopicDto;
    }

    private List<SubTopicDto> convertSubtopicToDto(List<SubTopic> subTopics) {
        return subTopics.stream().map(this::convertSubtopicToDto).collect(toList());
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private SubTopic findSubtopicByIdOrThrow(Long id) {
        return subTopicRepo.findById(id).orElseThrow(() -> {
            log.warn("Sub topic not found");
            return new ResourceNotFoundException("Sub Topic not found");
        });
    }

    private MainTopic getMainTopicById(Long id) {
        return mainTopicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Main Topic not found"));
    }
}
