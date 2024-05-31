package pro.sky.animal_shelter.entities;


import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class StarterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "helloText", nullable = false)
    private String helloTest;

    public StarterEntity() {
    }

    public StarterEntity(Long id, String helloTest) {
        this.id = id;
        this.helloTest = helloTest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHelloTest() {
        return helloTest;
    }

    public void setHelloTest(String helloTest) {
        this.helloTest = helloTest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarterEntity that = (StarterEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(helloTest, that.helloTest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, helloTest);
    }
}
