package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @JoinColumn(name = "grammar_id")
    private GrammaticalStructure grammaticalStructure;
}