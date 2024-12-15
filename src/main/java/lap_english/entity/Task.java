package lap_english.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "task") // nhiem vu
public class Task extends BaseEntity{
    private String description;
    @Enumerated(EnumType.STRING)
    private TypeTask type;
    private String keyFunUpdate;
    private double total;

}