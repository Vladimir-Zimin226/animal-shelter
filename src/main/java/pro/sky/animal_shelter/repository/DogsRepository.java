package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.entity.Users;

import java.util.List;

public interface DogsRepository extends JpaRepository<Dogs, Long> {

    Dogs findDogsById(long id);

    List<Dogs> findDogsByAtHomeIsTrue();
    List<Dogs> findDogsByFindCuratorIsTrue();
    List<Dogs> findDogsByFindOwnerIsTrue();

}
