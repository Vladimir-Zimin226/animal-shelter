package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Cats;

import java.util.List;
import java.util.Optional;

public interface CatsRepository extends JpaRepository<Cats, Long> {

    Optional<Cats> findById(long id);

    List<Cats> findCatsByAtHomeIsTrue();
    List<Cats> findCatsByFindCuratorIsTrue();
    List<Cats> findCatsByFindOwnerIsTrue();

}
