package lap_english.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "word")
@AllArgsConstructor
@NoArgsConstructor
public class Word extends BaseEntity {
    private String word;
    private String meaning;
    private String pronounceUK;
    private String pronounceUS;
    private String audioUkBlobName;
    private String audioUsBlobName;
    private String imageBlobName;
    @Enumerated(EnumType.STRING)
    private WordType type;
    @Enumerated(EnumType.STRING)
    private WordLevel level;
    private String example;
    @ManyToOne
    @JoinColumn(name = "sub_topic_id")
    private SubTopic subTopic;
}
