package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Probation;

public interface ProbationRepository extends JpaRepository<Probation, Long> {

}
