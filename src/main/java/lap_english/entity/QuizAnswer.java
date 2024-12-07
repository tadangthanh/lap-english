package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quiz_answer") // câu trả lời của bài quiz
public class QuizAnswer extends BaseEntity {
    private String answer;
    private boolean isCorrect;
    private String imgAnswer;
    @ManyToOne
    @JoinColumn(name = "custom_quiz_id")
    private CustomQuiz customQuiz;
}
