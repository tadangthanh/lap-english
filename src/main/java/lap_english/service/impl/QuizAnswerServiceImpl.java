package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.response.QuizAnswerResponse;
import lap_english.dto.request.QuizAnswerRequest;
import lap_english.entity.CustomQuiz;
import lap_english.entity.QuizAnswer;
import lap_english.mapper.QuizAnswerMapper;
import lap_english.repository.CustomQuizRepo;
import lap_english.repository.QuizAnswerRepo;
import lap_english.service.IAzureService;
import lap_english.service.IQuizAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class QuizAnswerServiceImpl implements IQuizAnswerService {
    private final QuizAnswerMapper quizAnswerMapper;
    private final QuizAnswerRepo quizAnswerRepo;
    private final CustomQuizRepo customQuizRepo;
    private final IAzureService azureService;


    @Override
    public void delete(Long id) {
        QuizAnswer quizAnswer = findQuizAnswerById(id);
        quizAnswerRepo.delete(quizAnswer);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_COMMITTED) {
                    // Xóa ảnh đã upload nếu transaction thành công
                    azureService.deleteBlob(quizAnswer.getImgAnswer());
                    log.info("Transaction committed successfully, image file deleted: {}", quizAnswer.getImgAnswer());
                }
            }
        });
    }

    @Override
    public QuizAnswerResponse save(QuizAnswerRequest quizAnswerRequest) {
        QuizAnswer quizAnswer = quizAnswerMapper.requestToEntity(quizAnswerRequest);
        // tìm câu hỏi của quiz
        CustomQuiz customQuiz = findCustomQuizById(quizAnswerRequest.getCustomQuizId());
        quizAnswer.setCustomQuiz(customQuiz);

        // Lưu câu trả lời tạm thời vào database
        quizAnswer = quizAnswerRepo.saveAndFlush(quizAnswer);
        // nếu k có ảnh thì return luôn
        if (quizAnswerRequest.getImgAnswer() == null) {
            return quizAnswerMapper.entityToResponse(quizAnswer);
        }
        // Biến tạm giữ tên blob để rollback nếu cần
        String uploadedImageBlobName;
        uploadedImageBlobName = azureService.upload(quizAnswerRequest.getImgAnswer());
        quizAnswer.setImgAnswer(uploadedImageBlobName); // Set đường dẫn blob vào QuizAnswer
        // Lưu lại quizAnswer với thông tin ảnh đã upload
        quizAnswer = quizAnswerRepo.saveAndFlush(quizAnswer);
        // Đăng ký TransactionSynchronization để xử lý sau khi transaction hoàn thành
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
        // Trả về response
        return quizAnswerMapper.entityToResponse(quizAnswer);
    }


    @Override
    public List<QuizAnswerResponse> getByQuizCustomId(Long id) {
        List<QuizAnswer> quizAnswers = quizAnswerRepo.findByCustomQuizId(id);
        return quizAnswerMapper.entityListToResponseList(quizAnswers);
    }

    @Override
    public QuizAnswerResponse update(QuizAnswerRequest quizAnswerRequest) {
        QuizAnswer quizAnswer = findQuizAnswerById(quizAnswerRequest.getId());
        quizAnswerMapper.updateFromRequest(quizAnswerRequest, quizAnswer);
        return quizAnswerMapper.entityToResponse(quizAnswerRepo.saveAndFlush(quizAnswer));
    }

    @Override
    public void deleteByCustomQuizId(Long customQuizId) {
        List<QuizAnswer> quizAnswers = quizAnswerRepo.findByCustomQuizId(customQuizId);
        quizAnswers.forEach(quizAnswer -> {
            String imgAnswer = quizAnswer.getImgAnswer();
            quizAnswerRepo.delete(quizAnswer);
            if (imgAnswer != null && !imgAnswer.isEmpty()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == TransactionSynchronization.STATUS_COMMITTED) {
                            // Xóa ảnh đã upload nếu transaction thành công
                            azureService.deleteBlob(imgAnswer);
                            log.info("Transaction committed successfully, image file deleted: {}", imgAnswer);
                        }
                    }
                });
            }
        });

    }

    private QuizAnswer findQuizAnswerById(Long id) {
        return quizAnswerRepo.findById(id).orElseThrow(() -> {
            log.error("Quiz answer not found");
            return new RuntimeException("Quiz answer not found");
        });
    }

    private CustomQuiz findCustomQuizById(Long id) {
        return customQuizRepo.findById(id).orElseThrow(() -> {
            log.error("Custom quiz not found");
            return new RuntimeException("Custom quiz not found");
        });
    }
}
