package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Dogs;

public interface DogsRepository extends JpaRepository<Dogs, Long> {

}
