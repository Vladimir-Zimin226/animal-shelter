package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.UserContact;

public interface UserContactRepository extends JpaRepository<UserContact,Long> {
}
