package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
