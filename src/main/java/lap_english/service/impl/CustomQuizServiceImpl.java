package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.request.CustomQuizRequest;
import lap_english.dto.response.CustomQuizResponse;
import lap_english.dto.response.PageResponse;
import lap_english.entity.CustomQuiz;
import lap_english.entity.ExerciseGrammar;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.CustomQuizMapper;
import lap_english.repository.CustomQuizRepo;
import lap_english.repository.ExerciseGrammarRepo;
import lap_english.service.IAzureService;
import lap_english.service.ICustomQuizService;
import lap_english.service.IQuizAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomQuizServiceImpl implements ICustomQuizService {
    private final CustomQuizMapper customQuizMapper;
    private final CustomQuizRepo customQuizRepo;
    private final IAzureService azureService;
    private final ExerciseGrammarRepo exerciseGrammarRepo;
    private final IQuizAnswerService quizAnswerService;


    @Override
    public CustomQuizResponse save(CustomQuizRequest customQuizRequest) {
        CustomQuiz customQuiz = customQuizMapper.requestToEntity(customQuizRequest);
        // tìm bài tập mà custom quiz thuộc về
//        ExerciseGrammar exerciseGrammar = findExerciseGrammarById(customQuizRequest.getExerciseGrammarId());
//        customQuiz.setExerciseGrammar(exerciseGrammar);
//        exerciseGrammar.setCustomQuiz(customQuiz);
        // lưu custom quiz
        customQuiz = customQuizRepo.saveAndFlush(customQuiz);
        // lưu các đáp án
        saveQuizAnswers(customQuizRequest, customQuiz.getId());
        // nếu câu hỏi ko có ảnh thì trả về ngay
        if (customQuizRequest.getImageQuestion() == null) {
            return customQuizMapper.entityToResponse(customQuiz);
        }
        // nếu câu hỏi có ảnh thì upload ảnh
        // Biến tạm giữ tên blob để rollback nếu cần
        String uploadedImageBlobName;
        // upload ảnh của quiz lên
        uploadedImageBlobName = azureService.upload(customQuizRequest.getImageQuestion());
        customQuiz.setImageQuestion(uploadedImageBlobName);
        customQuiz = customQuizRepo.save(customQuiz);

        String finalUploadedImageBlobName = uploadedImageBlobName;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED && finalUploadedImageBlobName != null) {
                    // Transaction không thành công, xóa ảnh đã upload
                    azureService.deleteBlob(finalUploadedImageBlobName);
                    log.error("Transaction rolled back, image file deleted: {}", finalUploadedImageBlobName);
                } else {
                    log.info("Transaction committed successfully with image: {}", finalUploadedImageBlobName);
                }
            }
        });
        return customQuizMapper.entityToResponse(customQuiz);
    }

    private void saveQuizAnswers(CustomQuizRequest customQuizRequest, Long customQuizId) {
        customQuizRequest.getQuizAnswers().forEach(quizAnswerRequest -> {
            quizAnswerRequest.setCustomQuizId(customQuizId);
            quizAnswerService.save(quizAnswerRequest);
        });
    }

    @Override
    public CustomQuizResponse update(CustomQuizRequest customQuizRequest) {
        CustomQuiz customQuiz = findCustomQuizById(customQuizRequest.getId());
        customQuizMapper.updateFromRequest(customQuizRequest, customQuiz);
        return customQuizMapper.entityToResponse(customQuiz);
    }

    @Override
    public CustomQuizResponse getById(Long id) {
        return customQuizMapper.entityToResponse(findCustomQuizById(id));
    }

    @Override
    public void delete(Long id) {
        CustomQuiz customQuiz = findCustomQuizById(id);
        String imageQuestion = customQuiz.getImageQuestion();
        quizAnswerService.deleteByCustomQuizId(id);
        customQuizRepo.delete(customQuiz);
        if (imageQuestion != null && !imageQuestion.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_COMMITTED) {
                        // Xóa ảnh đã upload nếu transaction thành công
                        azureService.deleteBlob(imageQuestion);
                        log.info("Transaction committed successfully, image file deleted: {}", imageQuestion);
                    }
                }
            });
        }
    }

    @Override
    public CustomQuizResponse getByExerciseGrammarId(Long exerciseGrammarId) {
        ExerciseGrammar exerciseGrammar = findExerciseGrammarById(exerciseGrammarId);
        return customQuizMapper.entityToResponse(exerciseGrammar.getCustomQuiz());
    }


    @Override
    public void deleteByExerciseGrammarId(Long exerciseGrammarId) {
        ExerciseGrammar exerciseGrammar = findExerciseGrammarById(exerciseGrammarId);
        CustomQuiz customQuiz = exerciseGrammar.getCustomQuiz();
        if (customQuiz == null) {
            log.warn("Custom Quiz not found with exercise grammar id: {}", exerciseGrammarId);
            throw new ResourceNotFoundException("Custom Quiz not found with exercise grammar id: " + exerciseGrammarId);
        }
        quizAnswerService.deleteByCustomQuizId(customQuiz.getId());
        delete(customQuiz.getId());
    }

    @Override
    public PageResponse<List<CustomQuizResponse>> getPage(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomQuiz> customQuizPage = customQuizRepo.findAll(pageRequest);
        List<CustomQuizResponse> customQuizResponses = customQuizMapper.entitiesToResponses(customQuizPage.getContent());
        return PageResponse.<List<CustomQuizResponse>>builder().items(customQuizResponses).totalItems(customQuizPage.getTotalElements()).totalPage(customQuizPage.getTotalPages()).hasNext(customQuizPage.hasNext()).pageNo(pageRequest.getPageNumber()).pageSize(pageRequest.getPageSize()).build();

    }

    private CustomQuiz findCustomQuizById(Long id) {
        return customQuizRepo.findById(id).orElseThrow(() -> {
            log.warn("Custom Quiz not found with id: {}", id);
            return new RuntimeException("Custom Quiz not found with id: " + id);
        });
    }

    private ExerciseGrammar findExerciseGrammarById(Long id) {
        return exerciseGrammarRepo.findById(id).orElseThrow(() -> {
            log.warn("Exercise Grammar not found with id: {}", id);
            return new RuntimeException("Exercise Grammar not found with id: " + id);
        });
    }
}
