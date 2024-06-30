package pro.sky.animal_shelter.service.implementations;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Cats;
import pro.sky.animal_shelter.exception.CatNotFoundException;
import pro.sky.animal_shelter.repository.CatsRepository;
import pro.sky.animal_shelter.service.services.CatService;

import java.util.List;
import java.util.Optional;

@Service
public class CatServiceImpl implements CatService {

    private final CatsRepository catsRepository;

    public CatServiceImpl(CatsRepository catsRepository) {
        this.catsRepository = catsRepository;
    }

    @Override
    public Cats createCat(Cats cat) {
        if (cat == null) {
            throw new CatNotFoundException();
        }
        return catsRepository.save(cat);
    }

    @Override
    public Optional<Cats> updateCat(long id, Cats cat) {
        Optional<Cats> optionalUpdatedCat = catsRepository.findById(id);

        if (optionalUpdatedCat.isEmpty()) {
            throw new CatNotFoundException();
        }

        Cats updatedCat = optionalUpdatedCat.get();
        updatedCat.setName(cat.getName());
        updatedCat.setBreed(cat.getBreed());
        updatedCat.setAge(cat.getAge());
        updatedCat.setFindCurator(cat.isFindCurator());
        updatedCat.setFindOwner(cat.isFindOwner());
        updatedCat.setHistory(cat.getHistory());
        updatedCat.setAtHome(cat.isAtHome());
        updatedCat.setImgPath(cat.getImgPath());  // Здесь я изменил, чтобы вызвался метод setImgPath из переданного объекта "cat"

        return Optional.of(catsRepository.save(updatedCat));
    }

    @Override
    public List<Cats> getAllCats() {
        return catsRepository.findAll();
    }

    @Override
    public void deleteCatById(Long catId) {
        if (!catsRepository.existsById(catId)) {
            throw new CatNotFoundException();
        }
        catsRepository.deleteById(catId);
    }

    @Override
    public List<Cats> getAllWhoFoundHome() {
        return catsRepository.findCatsByAtHomeIsTrue();
    }

    @Override
    public List<Cats> getAllWhoNeededCurator() {
        return catsRepository.findCatsByFindCuratorIsTrue();
    }

    @Override
    public List<Cats> getAllWhoNeededOwner() {
        return catsRepository.findCatsByFindOwnerIsTrue();
    }
}
