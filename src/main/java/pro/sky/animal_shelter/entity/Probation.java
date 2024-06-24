package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

//Таблица для отслеживания собак, находящихся у новых хозяев на испытательном сроке
@Entity
@Table (name="probation")
public class Probation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="relation_id")
    private Long relationId;

    @OneToOne
    @JoinColumn(name = "relationships_id")
    public Relationships relationships;

    @Column(name = "adoption_date")
    private LocalDate adoptionDate;


    /**можно использовать переменную и увеличивать ее на единицу раз в сутки,
     когда переменная равна 30 - испытательный срок окончен, запись удаляем
     */
    private int report;

    public Probation() {

    }

    public Probation(Long id, Long relationshipsId, Relationships relationships, LocalDate adoptionDate, int report) {
        this.id = id;
        this.relationId = relationshipsId;
        this.relationships = relationships;
        this.adoptionDate = adoptionDate;
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public Long getRelationshipsId() {
        return relationId;
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public LocalDate getAdoptionDate() {
        return adoptionDate;
    }

    public int getReport() {
        return report;
    }

    public void setRelationshipsId(Long relationshipsId) {
        this.relationId = relationshipsId;
    }

    public void setRelationships(Relationships relationships) {
        this.relationships = relationships;
    }

    public void setAdoptionDate(LocalDate adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public void setReport(int report) {
        this.report = report;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Probation probation = (Probation) o;
        return report == probation.report && Objects.equals(id, probation.id) && Objects.equals(relationId, probation.relationId) && Objects.equals(relationships, probation.relationships) && Objects.equals(adoptionDate, probation.adoptionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, relationId, relationships, adoptionDate, report);
    }
}
