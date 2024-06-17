package pro.sky.animal_shelter.service.services;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.exception.DogNotFoundException;

import java.util.List;

public interface DogService {

    /**
     * Создание нового песика и сохранение его в базу данных <br>
     * @see JpaRepository#save(Object)
     * @param dog объект класса {@link Dogs}, не может быть {@code null}
     * @return созданный объект класса {@link Dogs}
     */
    Dogs createDog(Dogs dog);

    /**
     * Редактирование песика и сохранение его в базу данных <br>
     * @see  JpaRepository#save(Object)
     * @param dog объект класса {@link Dogs}, не может быть {@code null}
     * @return отредактированный объект класса {@link Dogs}
     * @throws DogNotFoundException если песик не был найден в базе данных
     */
    Dogs updateDog(long id, Dogs dog);

    /**
     * Вывод списка всех собак из приюта из базы данных<br>
     * @see JpaRepository#findAll()
     * @return список объектов класса {@link Dogs}
     */
    List<Dogs> getAllDogs();

    /**
     * Удаление песика из базы данных по его {@code id} <br>
     * @see JpaRepository#deleteById(Object)
     * @param dogId идентификатор песика, которого нужно удалить из базы данных, не может быть {@code null}
     * @return {@code true} если объект был найден в базе данных, в противном случае {@link DogNotFoundException}
     */
    void deleteDogById(Long dogId);

    /**
     * Поиск песиков в базе данных по boolean-значению наличия дома {@code atHome} <br>
     * @return найденный список объектов класса {@link Dogs}
     */
    List<Dogs> getAllWhoFoundHome();

    /**
     * Поиск песиков в базе данных по boolean-значению поиска спонсора (куратора) {@code findCurator} <br>
     * @return найденный список объектов класса {@link Dogs}
     */
    List<Dogs> getAllWhoNeededCurator();

    /**
     * Поиск песиков в базе данных по boolean-значению поиска зозяина {@code findOwner} <br>
     * @return найденный список объектов класса {@link Dogs}
     */
    List<Dogs> getAllWhoNeededOwner();
}
