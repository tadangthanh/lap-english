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
@Table(name = "custom_quiz")
public class CustomQuiz extends BaseEntity {
    @Enumerated(value = EnumType.STRING)
    private TypeQuiz typeQuiz;
    private String question;
    private String imageQuestion;
    @OneToMany(mappedBy = "customQuiz", cascade = CascadeType.ALL)
    private List<QuizAnswer> quizAnswers;
}
