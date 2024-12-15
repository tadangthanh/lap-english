package lap_english.dto.request;

import lap_english.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult extends BaseDto {
    private boolean isLearned;
    private Long idObject;
    private TypeQuizResult type;
    private int total;
    private int totalSpeak;
    private int totalListen;
    private int totalRead;
    private int totalWrite;
    private int correct;
    private int correctSpeak;
    private int correctListen;
    private int correctWrite;
    private int correctRead;
    private int correctConsecutive;
    private int bonus;
    private int pointRank;
    private int totalWord;
    private int totalSentence;
    private int totalGrammar;
}
