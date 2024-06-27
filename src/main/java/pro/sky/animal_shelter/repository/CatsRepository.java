package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Cats;

import java.util.List;

public interface CatsRepository extends JpaRepository<Cats, Long> {

    Cats findCatsById(long id);

    List<Cats> findCatsByAtHomeIsTrue();
    List<Cats> findCatsByFindCuratorIsTrue();
    List<Cats> findCatsByFindOwnerIsTrue();

}
