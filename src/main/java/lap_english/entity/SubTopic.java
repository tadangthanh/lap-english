package lap_english.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sub_topic")
public class SubTopic extends BaseEntity {
    private String name;
    private String blobName;
    @ManyToOne
    @JoinColumn(name = "main_topic_id")
    private MainTopic mainTopic;
    @OneToMany(mappedBy = "subTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Word> words = new ArrayList<>();

    @OneToMany(mappedBy = "subTopic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sentence> sentences = new ArrayList<>();
    private boolean isWord;
    private int diamond;
    private int gold;
}
