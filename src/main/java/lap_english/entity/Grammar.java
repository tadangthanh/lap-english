package lap_english.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grammar")
@Setter
public class Grammar extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "type_grammar_id")
    private TypeGrammar typeGrammar;
}
