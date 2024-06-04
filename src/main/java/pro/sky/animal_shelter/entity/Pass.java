package pro.sky.animal_shelter.entity;


import jakarta.persistence.*;

import javax.swing.plaf.nimbus.State;
import java.util.Objects;

@Entity
public class Pass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "d_of_b", nullable = false)
    private String dateOfBirth;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    public Pass() {
    }

    public Pass(Long id, String fullName, String dateOfBirth, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pass pass = (Pass) o;
        return Objects.equals(id, pass.id) && Objects.equals(fullName, pass.fullName) && Objects.equals(dateOfBirth, pass.dateOfBirth) && Objects.equals(phoneNumber, pass.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, dateOfBirth, phoneNumber);
    }
}
