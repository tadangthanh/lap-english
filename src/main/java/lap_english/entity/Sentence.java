package lap_english.entity;

import jakarta.persistence.*;
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
    @Column(columnDefinition = "TEXT")
    private String sentence;
    @Column(columnDefinition = "TEXT")
    private String translation;
    @ManyToOne
    @JoinColumn(name = "sub_topic_id")
    private SubTopic subTopic;
}
