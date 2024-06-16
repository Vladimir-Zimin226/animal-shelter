package pro.sky.animal_shelter.service.services;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.exception.UserNotFoundException;

import java.util.List;

public interface UserService {

    /**
     * Создание нового пользователя и сохранение его в базу данных <br>
     *
     * @param user объект класса {@link Users}, не может быть {@code null}
     * @return созданный объект класса {@link Users}
     * @see JpaRepository#save(Object)
     */
    Users createUser(Users user);

    /**
     * Редактирование пользователя и сохранение его в базу данных <br>
     *
     * @param user объект класса {@link Users}, не может быть {@code null}
     * @return отредактированный объект класса {@link Users}
     * @throws UserNotFoundException если пользователь не был найден в базе данных
     * @see JpaRepository#save(Object)
     */
    Users updateUser(long id, Users user);

    /**
     * Вывод списка всех пользователей телеграм-бота из базы данных<br>
     *
     * @return список объектов класса {@link Users}
     * @see JpaRepository#findAll()
     */
    List<Users> getAllUsers();

    /**
     * Удаление пользователя из базы данных по его {@code id} <br>
     *
     * @param userId идентификатор пользователя, которого нужно удалить из базы данных, не может быть {@code null}
     * @return {@code true} если объект был найден в базе данных, в противном случае {@link UserNotFoundException}
     * @see JpaRepository#deleteById(Object)
     */
    void deleteUserById(Long userId);

    /**
     * Поиск пользователей в базе данных по boolean-значению принадлежности к волонтерам {@code isVolunteer} <br>
     *
     * @return найденный список объектов класса {@link Dogs}
     */
    List<Users> getAllVolunteer();

    Users findAnyVolunteerFromUsers();

    Users findUserByTelegramId(String telegramId);
}
