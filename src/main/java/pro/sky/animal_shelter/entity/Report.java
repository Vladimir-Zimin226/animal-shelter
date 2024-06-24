package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "report_table")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "photo")
    private String photoOfPet;

    @Column(name = "diet", length = 1024)
    private String diet;

    @Column(name = "well_being", length = 1024)
    private String wellBeing;

    @Column(name = "behavior_changes", length = 1024)
    private String behaviorChanges;

    @Column(name = "create_date")
    private LocalDate date;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getPhotoOfPet() {
        return photoOfPet;
    }

    public void setPhotoOfPet(String photoOfPet) {
        this.photoOfPet = photoOfPet;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getWellBeing() {
        return wellBeing;
    }

    public void setWellBeing(String wellBeing) {
        this.wellBeing = wellBeing;
    }

    public String getBehaviorChanges() {
        return behaviorChanges;
    }

    public void setBehaviorChanges(String behaviorChanges) {
        this.behaviorChanges = behaviorChanges;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report report)) return false;
        return Objects.equals(getId(), report.getId()) && Objects.equals(getUser(), report.getUser()) && Objects.equals(getPhotoOfPet(), report.getPhotoOfPet()) && Objects.equals(getDiet(), report.getDiet()) && Objects.equals(getWellBeing(), report.getWellBeing()) && Objects.equals(getBehaviorChanges(), report.getBehaviorChanges()) && Objects.equals(date, report.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getPhotoOfPet(), getDiet(), getWellBeing(), getBehaviorChanges(), date);
    }
}
