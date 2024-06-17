package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.animal_shelter.entity.PhotoOfPet;

public interface PhotoOfPetRepository extends JpaRepository<PhotoOfPet, Long> {
}
