package pro.sky.animal_shelter.service.implementations;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.exception.DogNotFoundException;
import pro.sky.animal_shelter.repository.DogsRepository;
import pro.sky.animal_shelter.service.services.DogService;

import java.util.List;

@Service
public class DogServiceImpl implements DogService {

    private final DogsRepository dogsRepository;

    public DogServiceImpl(DogsRepository dogsRepository) {
        this.dogsRepository = dogsRepository;
    }

    @Override
    public Dogs createDog(Dogs dog) {
        if (dog == null) {
            throw new DogNotFoundException();
        }
        return dogsRepository.save(dog);
    }

    @Override
    public Dogs updateDog(long id, Dogs dog) {
        Dogs updatedDog = dogsRepository.findDogsById(id);
        if (updatedDog == null) {
            throw new DogNotFoundException();
        }
        updatedDog.setName(dog.getName());
        updatedDog.setBreed(dog.getBreed());
        updatedDog.setAge(dog.getAge());
        updatedDog.setFindCurator(dog.isFindCurator());
        updatedDog.setFindOwner(dog.isFindOwner());
        updatedDog.setHistory(dog.getHistory());
        updatedDog.setAtHome(dog.isAtHome());
        dog.setImgPath(updatedDog.getImgPath());
        return dogsRepository.save(updatedDog);
    }

    @Override
    public List<Dogs> getAllDogs() {
        return dogsRepository.findAll();
    }

    @Override
    public void deleteDogById(Long dogId) {
        if (!dogsRepository.existsById(dogId)) {
            throw new DogNotFoundException();
        }
        dogsRepository.deleteById(dogId);
    }

    @Override
    public List<Dogs> getAllWhoFoundHome() {
        return dogsRepository.findDogsByAtHomeIsTrue();
    }

    @Override
    public List<Dogs> getAllWhoNeededCurator() {
        return dogsRepository.findDogsByFindCuratorIsTrue();
    }

    @Override
    public List<Dogs> getAllWhoNeededOwner() {
        return dogsRepository.findDogsByFindOwnerIsTrue();
    }
}
