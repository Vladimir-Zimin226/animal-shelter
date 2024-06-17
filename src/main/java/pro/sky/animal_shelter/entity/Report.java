package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;
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

    @OneToOne
    @JoinColumn(name = "photo_id")
    private PhotoOfPet photoOfPet;

    @Column(name = "diet", length = 1024)
    private String diet;

    @Column(name = "well_being", length = 1024)
    private String wellBeing;

    @Column(name = "behavior_changes", length = 1024)
    private String behaviorChanges;

    // Getters and Setters

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

    public PhotoOfPet getPhotoOfPet() {
        return photoOfPet;
    }

    public void setPhotoOfPet(PhotoOfPet photoOfPet) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(user, report.user) && Objects.equals(photoOfPet, report.photoOfPet) && Objects.equals(diet, report.diet) && Objects.equals(wellBeing, report.wellBeing) && Objects.equals(behaviorChanges, report.behaviorChanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, photoOfPet, diet, wellBeing, behaviorChanges);
    }
}
