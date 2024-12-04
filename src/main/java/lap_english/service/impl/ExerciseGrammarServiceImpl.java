package lap_english.service.impl;

import jakarta.transaction.Transactional;
import lap_english.dto.CustomQuizResponse;
import lap_english.dto.QuizAnswerResponse;
import lap_english.dto.request.ExerciseGrammarRequest;
import lap_english.dto.response.ExerciseGrammarResponse;
import lap_english.dto.response.PageResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
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
    private final CustomQuizRepo  customQuizRepo;
    private final IQuizAnswerService quizAnswerService;


    @Override
    public void delete(Long id) {

    }

    @Override
    public ExerciseGrammarResponse save(ExerciseGrammarRequest exerciseGrammarRequest) {
        ExerciseGrammar exerciseGrammar = new ExerciseGrammar();
        GrammaticalStructure grammaticalStructure = findGrammaticalStructureById(exerciseGrammarRequest.getGrammaticalStructureId());
        exerciseGrammar.setGrammaticalStructure(grammaticalStructure);
        // luu bai tap
        exerciseGrammar = exerciseGrammarRepo.saveAndFlush(exerciseGrammar);
        // lưu custom quiz
        CustomQuizResponse customQuizResponse = customQuizService.save(exerciseGrammarRequest.getCustomQuiz(), exerciseGrammar.getId());


        List<QuizAnswerResponse> quizAnswerResponses = quizAnswerService.getByQuizCustomId(customQuizResponse.getId());
        customQuizResponse.setQuizAnswers(quizAnswerResponses);
        ExerciseGrammarResponse exerciseGrammarResponse = exerciseGrammarMapper.entityToResponse(exerciseGrammar);
        exerciseGrammarResponse.setCustomQuiz(customQuizResponse);
        return exerciseGrammarResponse;
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] exercises) {
        return null;
    }

    private GrammaticalStructure findGrammaticalStructureById(Long id) {
        return grammaticalStructureRepo.findById(id).orElseThrow(() -> {
            log.warn("GrammaticalStructure not found by id: {}", id);
            return new ResourceNotFoundException("GrammaticalStructure not found");
        });
    }
    private CustomQuiz findCustomQuizById(Long id){
        return customQuizRepo.findById(id).orElseThrow(() -> {
            log.warn("customQuiz not found by id: {}", id);
            return new ResourceNotFoundException("customQuiz not found");
        });
    }
}
