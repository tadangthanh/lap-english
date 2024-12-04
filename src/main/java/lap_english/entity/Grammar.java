package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grammar")
public class Grammar extends BaseEntity {
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "type_grammar_id")
    private TypeGrammar typeGrammar;
}
