package pro.sky.animal_shelter.service.services;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.entity.Users;

import java.util.List;

public interface UserService {

    /**
     * Создание нового пользователя и сохранение его в базу данных <br>
     * @see JpaRepository#save(Object)
     * @param user объект класса {@link Users}, не может быть {@code null}
     * @return созданный объект класса {@link Users}
     */
    Users createUser(Users user);

    /**
     * Редактирование пользователя и сохранение его в базу данных <br>
     * @see  JpaRepository#save(Object)
     * @param user объект класса {@link Users}, не может быть {@code null}
     * @return отредактированный объект класса {@link Users}
     * @throws IllegalArgumentException если пользователь не был найден в базе данных
     */
    Users updateUser(long id, Users user);

    /**
     * Вывод списка всех пользователей телеграм-бота из базы данных<br>
     * @see JpaRepository#findAll()
     * @return список объектов класса {@link Users}
     */
    List<Users> getAllUsers();

    /**
     * Удаление пользователя из базы данных по его {@code id} <br>
     * @see JpaRepository#deleteById(Object)
     * @param userId идентификатор пользователя, которого нужно удалить из базы данных, не может быть {@code null}
     * @return {@code true} если объект был найден в базе данных, в противном случае {@link IllegalArgumentException}
     */
    void deleteUserById(Long userId);

    /**
     * Поиск пользователей в базе данных по boolean-значению принадлежности к волонтерам {@code isVolunteer} <br>
     * @return найденный список объектов класса {@link Dogs}
     */
    List<Users> getAllVolunteer();
}
