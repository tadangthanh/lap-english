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
@Table(name = "grammatical_structure")
public class GrammaticalStructure extends BaseEntity {
    private String description;
    @Column(name = "structure", nullable = false)
    private String structure;
    @ManyToOne
    @JoinColumn(name = "grammar_id")
    private Grammar grammar;
    @OneToMany(mappedBy = "grammaticalStructure")
    private List<ExerciseGrammar> exerciseGrammars;
}
