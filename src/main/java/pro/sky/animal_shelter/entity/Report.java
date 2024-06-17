package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "report_table")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Many reports can belong to one user
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Lob
    @Column(name = "photo_of_pet", nullable = false)
    private byte[] photoOfPet;

    @Column(name = "diet", length = 1024)
    private String diet;

    @Column(name = "well_being", length = 1024)
    private String wellBeing;

    @Column(name = "behavior_changes", length = 1024)
    private String behaviorChanges;

    public Report() {
    }

    public Report(Users user, byte[] photoOfPet, String diet, String wellBeing, String behaviorChanges) {
        this.user = user;
        this.photoOfPet = photoOfPet;
        this.diet = diet;
        this.wellBeing = wellBeing;
        this.behaviorChanges = behaviorChanges;
    }

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

    public byte[] getPhotoOfPet() {
        return photoOfPet;
    }

    public void setPhotoOfPet(byte[] photoOfPet) {
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
        return Objects.equals(id, report.id) && Objects.equals(user, report.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }
}
