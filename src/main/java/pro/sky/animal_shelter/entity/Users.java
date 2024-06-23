package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String telegramId;

    private String phoneNumber;

    private boolean isVolunteer;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    public Users() {
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isVolunteer() {
        return isVolunteer;
    }

    public void setVolunteer(boolean volunteer) {
        isVolunteer = volunteer;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public void addReport(Report report) {
        reports.add(report);
        report.setUser(this);
    }

    public void removeReport(Report report) {
        reports.remove(report);
        report.setUser(null);
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
        return Objects.hash(id);
    }
}
