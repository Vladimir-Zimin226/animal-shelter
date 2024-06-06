package pro.sky.animal_shelter.service;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.repository.DogsRepository;

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
            throw new IllegalArgumentException("Dogs cannot be null");
        }
        return dogsRepository.save(dog);
    }

    @Override
    public Dogs updateDog(long id, Dogs dog) {
        Dogs updatedDog = dogsRepository.findDogsById(id);
        if (updatedDog == null) {
            throw new IllegalArgumentException("Dog not found with id: " + id);
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
            throw new IllegalArgumentException("Dog not found with id: " + dogId);
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
