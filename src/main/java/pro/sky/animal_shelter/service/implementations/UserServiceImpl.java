package pro.sky.animal_shelter.service.implementations;

import org.springframework.stereotype.Service;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.exception.UserNotFoundException;
import pro.sky.animal_shelter.repository.UsersRepository;
import pro.sky.animal_shelter.service.services.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    public UserServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public Users createUser(Users user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
        return usersRepository.save(user);
    }

    @Override
    public Users updateUser(long id, Users user) {
        Users updatedUser = usersRepository.findUsersById(id);
        if (updatedUser == null) {
            throw new UserNotFoundException();
        }
        updatedUser.setName(user.getName());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        return usersRepository.save(updatedUser);
    }


    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        usersRepository.deleteById(userId);
    }

    @Override
    public List<Users> getAllVolunteer() {
        return usersRepository.findUsersByIsVolunteerIsTrue();
    }

    @Override
    public Users findUserByTelegramId(String telegramId) {
        return usersRepository.findUserByTelegramId(telegramId);
    }

    @Override
    public Users findAnyVolunteerFromUsers() {
        return usersRepository.findAnyVolunteerForConsultansy();
    }

}
