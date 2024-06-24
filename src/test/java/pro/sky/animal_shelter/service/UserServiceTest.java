package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.entity.Users;
import org.junit.jupiter.api.Test;
import pro.sky.animal_shelter.exception.UserNotFoundException;
import pro.sky.animal_shelter.repository.UsersRepository;
import pro.sky.animal_shelter.service.implementations.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserServiceImpl userService;

    Users vova = new Users(1L, "Вова", "VovaTheGreat", "+79585190011", true);
    Users saha = new Users(2L, "Саша", "SahaTheGreat", "+79083659643", false);


    public static final List<Users> USERS_LIST = List.of(
            new Users(1L, "Вова", "VovaTheGreat", "+79585190011", true),
            new Users(2L, "Саша", "SahaTheGreat", "+79083659643", true),
            new Users(3L, "Гена", "GenaTheGreat", "+79083659643", false)
            );

    @Test
    void getAllUserTest(){
        when(usersRepository.findAll())
                .thenReturn(USERS_LIST);

        assertIterableEquals(USERS_LIST, userService.getAllUsers());
    }

    @Test
    public void createUserTest(){
        when(userService.createUser(vova)).thenReturn(vova);
        assertEquals(userService.createUser(vova), vova);
    }

    @Test
    void createUser_NulUser_ShouldThrowExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.createUser(null);
        });
    }

    @Test
    public void updateUserTest() {
        long id = 1L;

        Users updateUsers = vova;

        when(usersRepository.findUsersById(id)).thenReturn(vova);

        when(usersRepository.save(any(Users.class))).thenReturn(vova);
        Users result = userService.updateUser(id, vova);

        assertEquals(updateUsers, result);
    }

    @Test
    void updateUser_NonExistentUser_ShouldThrowExceptionTest() {

        when(usersRepository.findUsersById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(1L, vova);
        });
    }

    @Test
    void deleteUsersByIdTest() {

        when(usersRepository.existsById(1L)).thenReturn(true);

        userService.deleteUserById(1L);

        verify(usersRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserById_NonExistentUser_ShouldThrowExceptionTest() {
        when(usersRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUserById(1L);
        });
    }

    @Test
    void getAllVolunteerTest() {

        when(usersRepository.findUsersByIsVolunteerIsTrue()).thenReturn(USERS_LIST);

        List<Users> result = userService.getAllVolunteer();

        assertEquals(USERS_LIST, result);
        verify(usersRepository, times(1)).findUsersByIsVolunteerIsTrue();

    }

    @Test
    void getFindUserByTelegramIdTest() {
        when(usersRepository.findUserByTelegramId("VovaTheGreat")).thenReturn(vova);
        Users actualUsers = userService.findUserByTelegramId("VovaTheGreat");
        assertEquals(vova, actualUsers);
    }

    @Test
    void getFindAnyVolunteerFromUsersTest(){
        when(userService.findAnyVolunteerFromUsers()).thenReturn(vova, saha);
        Users volonter = userService.findAnyVolunteerFromUsers();
        assertTrue(volonter.isVolunteer());
    }
}

