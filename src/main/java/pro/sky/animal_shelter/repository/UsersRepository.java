package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Users;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Users findUsersById(long id);

    List<Users> findUsersByIsVolunteerIsTrue();

}
