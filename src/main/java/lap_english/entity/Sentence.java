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
@Table(name = "sentence")
public class Sentence extends BaseEntity {
    private String sentence;
    private String translation;
    @ManyToOne
    @JoinColumn(name = "sub_topic_id")
    private SubTopic subTopic;
}
