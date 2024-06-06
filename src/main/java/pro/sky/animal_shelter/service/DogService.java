package pro.sky.animal_shelter.service;

import pro.sky.animal_shelter.entity.Dogs;

import java.util.List;

public interface DogService {
    Dogs createUser(Dogs dog);

    Dogs updateDog(long id, Dogs dog);

    List<Dogs> getAllDogs();

    void deleteDogById(Long dogId);

    List<Dogs> getAllWhoFoundHome();

    List<Dogs> getAllWhoNeededCurator();

    List<Dogs> getAllWhoNeededOwner();
}
