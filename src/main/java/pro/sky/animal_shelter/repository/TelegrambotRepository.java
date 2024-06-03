package pro.sky.animal_shelter.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Pass;

public interface TelegrambotRepository extends JpaRepository<Pass,Long> {

}
