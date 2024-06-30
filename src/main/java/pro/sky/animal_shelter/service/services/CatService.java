package pro.sky.animal_shelter.service.services;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Cats;
import pro.sky.animal_shelter.exception.CatNotFoundException;

import java.util.List;
import java.util.Optional;

public interface CatService {

    /**
     * Создание новой кошки и сохранение её в базу данных <br>
     * @see JpaRepository#save(Object)
     * @param cat объект класса {@link Cats}, не может быть {@code null}
     * @return созданный объект класса {@link Cats}
     */
    Cats createCat(Cats cat);

    /**
     * Редактирование кошки и сохранение её в базу данных <br>
     * @see  JpaRepository#save(Object)
     * @param cat объект класса {@link Cats}, не может быть {@code null}
     * @return отредактированный объект класса {@link Cats}
     * @throws CatNotFoundException если кошка не была найдена в базе данных
     */
    Optional<Cats> updateCat(long id, Cats cat);

    /**
     * Вывод списка всех кошек из приюта из базы данных<br>
     * @see JpaRepository#findAll()
     * @return список объектов класса {@link Cats}
     */
    List<Cats> getAllCats();

    /**
     * Удаление кошки из базы данных по её {@code id} <br>
     * @see JpaRepository#deleteById(Object)
     * @param catId идентификатор кошки, которую нужно удалить из базы данных, не может быть {@code null}
     * @return {@code true} если объект был найден в базе данных, в противном случае {@link CatNotFoundException}
     */
    void deleteCatById(Long catId);

    /**
     * Поиск кошек в базе данных по boolean-значению наличия дома {@code atHome} <br>
     * @return найденный список объектов класса {@link Cats}
     */
    List<Cats> getAllWhoFoundHome();

    /**
     * Поиск кошек в базе данных по boolean-значению поиска спонсора (куратора) {@code findCurator} <br>
     * @return найденный список объектов класса {@link Cats}
     */
    List<Cats> getAllWhoNeededCurator();

    /**
     * Поиск кошек в базе данных по boolean-значению поиска хозяина {@code findOwner} <br>
     * @return найденный список объектов класса {@link Cats}
     */
    List<Cats> getAllWhoNeededOwner();

}
