package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;
import java.util.Objects;

//Таблица отображает все взаимоотношения между пользователями и животными
@Entity
@Table(name="relationships")
public class Relationships {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="relationships_id")
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="dog_id")
    private Long animalId;

    //Опекун в приюте, либо хозяин
    private String relation;

    //Находится на курации (испытательный период)
    private boolean curation;

    public Relationships() {

    }

    public Relationships(Long id, Long userId, Long animalId, String relation, boolean curation) {
        this.id = id;
        this.userId = userId;
        this.animalId = animalId;
        this.relation = relation;
        this.curation = curation;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public String getRelation() {
        return relation;
    }

    public boolean isCuration() {
        return curation;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public void setCuration(boolean curation) {
        this.curation = curation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationships that = (Relationships) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
