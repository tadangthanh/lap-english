package lap_english.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exercise_grammar")
public class ExerciseGrammar extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "grammatical_structure_id")
    private GrammaticalStructure grammaticalStructure;
    @OneToOne
    @JoinColumn(name = "custom_quiz_id")
    private CustomQuiz customQuiz;
}
