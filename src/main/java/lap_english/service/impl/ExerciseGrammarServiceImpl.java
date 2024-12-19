package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.GrammaticalStructureDto;
import lap_english.dto.request.CustomQuizRequest;
import lap_english.dto.request.ExerciseGrammarRequest;
import lap_english.dto.response.CustomQuizResponse;
import lap_english.dto.response.ExerciseGrammarResponse;
import lap_english.dto.response.PageResponse;
import lap_english.dto.response.QuizAnswerResponse;
import lap_english.entity.CustomQuiz;
import lap_english.entity.ExerciseGrammar;
import lap_english.entity.GrammaticalStructure;
import lap_english.exception.ResourceNotFoundException;
import lap_english.mapper.ExerciseGrammarMapper;
import lap_english.repository.CustomQuizRepo;
import lap_english.repository.ExerciseGrammarRepo;
import lap_english.repository.GrammaticalStructureRepo;
import lap_english.service.ICustomQuizService;
import lap_english.service.IExerciseGrammarService;
import lap_english.service.IQuizAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ExerciseGrammarServiceImpl implements IExerciseGrammarService {
    private final ICustomQuizService customQuizService;
    private final ExerciseGrammarRepo exerciseGrammarRepo;
    private final GrammaticalStructureRepo grammaticalStructureRepo;
    private final ExerciseGrammarMapper exerciseGrammarMapper;
    private final CustomQuizRepo customQuizRepo;
    private final IQuizAnswerService quizAnswerService;
    private final CustomQuizServiceImpl customQuizServiceImpl;

    @Override
    public void delete(Long id) {
        // xóa các câu hỏi của bài tập ngữ pháp
        customQuizServiceImpl.deleteByExerciseGrammarId(id);
        ExerciseGrammar exerciseGrammar = exerciseGrammarRepo.findById(id).orElseThrow(() -> {
            log.warn("ExerciseGrammar not found by id: {}", id);
            return new ResourceNotFoundException("ExerciseGrammar not found");
        });
        exerciseGrammarRepo.delete(exerciseGrammar);
    }

    /**
     * @param exerciseGrammarRequest bài tập ngữ pháp, bao gồm id của cấu trúc ngữ pháp và 1 bài tập quizz
     *                               1 cấu trúc ngữ pháp có nhiều exercise,
     *                               1 exercise có 1 custom quiz
     *                               1 custom quiz có nhiều đáp án để chọn
     * @return
     */
    @Override
    public ExerciseGrammarResponse save(ExerciseGrammarRequest exerciseGrammarRequest) {
        ExerciseGrammar exerciseGrammar = new ExerciseGrammar();
        // tìm và set grammaticalStructure
        GrammaticalStructure grammaticalStructure = findGrammaticalStructureById(exerciseGrammarRequest.getGrammaticalStructureId());
        exerciseGrammar.setGrammaticalStructure(grammaticalStructure);
        // luu bai tap
        exerciseGrammar = exerciseGrammarRepo.saveAndFlush(exerciseGrammar);
        // lưu custom quizRequest
        exerciseGrammarRequest.setId(exerciseGrammar.getId());
//        CustomQuizResponse customQuizResponse = saveCustomQuizRequestByExerciseGrammarRequest(exerciseGrammarRequest);
        // luu va set customQuiz cho bai tap vua luu de tra ve cho client
        ExerciseGrammarResponse exerciseGrammarResponse = exerciseGrammarMapper.entityToResponse(exerciseGrammar);
//        exerciseGrammarResponse.setCustomQuiz(customQuizResponse);
        return exerciseGrammarResponse;
    }

//    private CustomQuizResponse saveCustomQuizRequestByExerciseGrammarRequest(ExerciseGrammarRequest exerciseGrammarRequest) {
//        // lưu câu hỏi quiz
//        CustomQuizRequest customQuizRequest = exerciseGrammarRequest.getCustomQuiz();
//        customQuizRequest.setExerciseGrammarId(exerciseGrammarRequest.getId());
//        CustomQuizResponse customQuizResponse = customQuizService.save(customQuizRequest);
//        // lấy danh sách các câu trả lời của câu hỏi
//        List<QuizAnswerResponse> quizAnswerResponses = quizAnswerService.getByQuizCustomId(customQuizResponse.getId());
//        // set danh sách câu trả lời cho câu hỏi
//        customQuizResponse.setQuizAnswers(quizAnswerResponses);
//        return customQuizResponse;
//    }

    @Override
    public PageResponse<List<ExerciseGrammarResponse>> getByGrammaticalStructureId(Long grammaticalStructureId, Integer page, Integer size) {
        // lay danh sach excercise cua grammaticalstructure
        PageRequest pageable = PageRequest.of(page, size);
        Page<ExerciseGrammar> exerciseGrammarPage = exerciseGrammarRepo.findByGrammaticalStructureId(grammaticalStructureId, pageable);
        List<ExerciseGrammar> exerciseGrammarList = exerciseGrammarPage.getContent();
        List<ExerciseGrammarResponse> exerciseGrammarResponses = exerciseGrammarList.stream().map(exerciseGrammarMapper::entityToResponse).toList();

        // lấy câu hỏi của exercise và đáp án của các câu hỏi đó và set lại cho exercise
        exerciseGrammarResponses.forEach(e -> {
            CustomQuizResponse customQuizResponse = customQuizService.getByExerciseGrammarId(e.getId());
            e.setCustomQuiz(customQuizResponse);
        });
        return PageResponse.<List<ExerciseGrammarResponse>>builder().items(exerciseGrammarResponses).totalItems(exerciseGrammarPage.getTotalElements()).totalPage(exerciseGrammarPage.getTotalPages()).hasNext(exerciseGrammarPage.hasNext()).pageNo(pageable.getPageNumber()).pageSize(pageable.getPageSize()).build();
    }

    @Override
    public void deleteByGrammaticalStructureId(Long grammaticalStructureId) {
        List<ExerciseGrammar> exerciseGrammarList = exerciseGrammarRepo.findByGrammaticalStructureId(grammaticalStructureId);
        exerciseGrammarList.forEach(e -> {
            delete(e.getId());
        });
    }


    private GrammaticalStructure findGrammaticalStructureById(Long id) {
        return grammaticalStructureRepo.findById(id).orElseThrow(() -> {
            log.warn("GrammaticalStructure not found by id: {}", id);
            return new ResourceNotFoundException("GrammaticalStructure not found");
        });
    }

    private CustomQuiz findCustomQuizById(Long id) {
        return customQuizRepo.findById(id).orElseThrow(() -> {
            log.warn("customQuiz not found by id: {}", id);
            return new ResourceNotFoundException("customQuiz not found");
        });
    }
}
