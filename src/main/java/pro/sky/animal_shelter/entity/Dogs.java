package pro.sky.animal_shelter.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table (name="dogs")
public class Dogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int age;

    private String breed;

    //Особенности собаки: заболевания, не ладит с другими животными, пристраивается только в частый дом и т.д.
    private String specifics;

    private String history;

    /** Собака может:
     *  нуждаться в опекуне, без варианта пристроя
     *  искать и опекуна(до момента пристроя), и хозяина
     *  иметь опекуна в приюте и искать хозяина
    */
    private boolean findCurator;

    private boolean findOwner;

    //Дефолтное значение false
    private boolean atHome;

    private String imgPath;

    public Dogs() {

    }

    public Dogs(Long id, String name, int age, String breed, String specifics, String history,
                boolean findCurator, boolean findOwner, boolean atHome, String imgPath) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.specifics = specifics;
        this.history = history;
        this.findCurator = findCurator;
        this.findOwner = findOwner;
        this.atHome = atHome;
        this.imgPath = imgPath;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getBreed() {
        return breed;
    }

    public String getSpecifics() {
        return specifics;
    }

    public String getHistory() {
        return history;
    }

    public boolean isFindCurator() {
        return findCurator;
    }

    public boolean isFindOwner() {
        return findOwner;
    }

    public boolean isAtHome() {
        return atHome;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public void setFindCurator(boolean findCurator) {
        this.findCurator = findCurator;
    }

    public void setFindOwner(boolean findOwner) {
        this.findOwner = findOwner;
    }

    public void setAtHome(boolean atHome) {
        this.atHome = atHome;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dogs dogs = (Dogs) o;
        return Objects.equals(id, dogs.id);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
