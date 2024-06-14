package pro.sky.animal_shelter.entity;


import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    private String telegramId;

    private String phoneNumber;

    private boolean isVolunteer;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="relationships",
            joinColumns=  @JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="dog_id", referencedColumnName="id") )
    private List<Dogs> dogs = new ArrayList<Dogs>();

    public Users() {}

    public Users(Long id, String name, String telegramId, String phoneNumber, boolean isVolunteer) {
        this.id = id;
        this.name = name;
        this.telegramId = telegramId;
        this.phoneNumber = phoneNumber;
        this.isVolunteer = isVolunteer;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setVolunteer(boolean volunteer) {
        isVolunteer = volunteer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(id, users.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
