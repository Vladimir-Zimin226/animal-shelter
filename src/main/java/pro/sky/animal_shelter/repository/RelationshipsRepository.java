package pro.sky.animal_shelter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Relationships;

public interface RelationshipsRepository extends JpaRepository<Relationships, Long> {
}