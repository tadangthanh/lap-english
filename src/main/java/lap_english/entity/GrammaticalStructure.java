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
@Table(name = "grammatical_structure")
public class GrammaticalStructure extends BaseEntity {
    private String description;
    private String structure;
    @ManyToOne
    @JoinColumn(name = "grammar_id")
    private Grammar grammar;
}
