package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "topic_chat")
@AllArgsConstructor
@NoArgsConstructor
public class TopicChat extends BaseEntity {
    private String name;
    private String description;
    private String blobName;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
