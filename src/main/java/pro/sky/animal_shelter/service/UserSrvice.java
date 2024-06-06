package pro.sky.animal_shelter.service;

import pro.sky.animal_shelter.entity.Users;

import java.util.List;

public interface UserSrvice {
    Users createUser(Users user);

    Users updateUser(long id, Users user);

    List<Users> getAllUsers();

    void deleteUserById(Long userId);

    List<Users> getAllVolunteer();
}
