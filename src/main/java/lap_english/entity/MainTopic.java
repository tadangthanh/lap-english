package lap_english.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "main_topic")
public class MainTopic extends BaseEntity {
    private String name;
    @OneToMany(mappedBy = "mainTopic", cascade = CascadeType.ALL)
    private Set<SubTopic> subTopics;
    private int diamound;
    private int gold;
    private boolean isWord;
}
