package lap_english.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "custom_quiz") // câu hỏi quiz
public class CustomQuiz extends BaseEntity {
    @Enumerated(value = EnumType.STRING)
    private TypeQuiz typeQuiz;
    private String question;
    private String imageQuestion;
    // danh sách các câu trả lời của bài quiz này
    @OneToMany(mappedBy = "customQuiz")
    private List<QuizAnswer> quizAnswers;
    @OneToOne(mappedBy = "customQuiz")
    private ExerciseGrammar exerciseGrammar;
}
