package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.exception.DogNotFoundException;
import pro.sky.animal_shelter.repository.DogsRepository;
import pro.sky.animal_shelter.service.implementations.DogServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DogServiceTest {

    @Mock
    private DogsRepository dogsRepository;
    @InjectMocks
    private DogServiceImpl dogService;

    Dogs sharik = new Dogs(10L, "Шарик", 3, "дворняга", "любит людей", "найден под забором", true, true, false, "media/nn-mj-2ENlyHquUX.png");
    Dogs tuzik = new Dogs(11L, "Тузик", 4, "хаски", "любит детей", "найден у заброшки", true, false, false, "media/nn-mj-2ENlyHquUX.png");

    public static final List<Dogs> DOGS_LIST = List.of(
            new Dogs(100L, "Шарик", 3, "дворняга", "любит людей", "найден под забором", true, true, false, "media/nn-mj-2ENlyHquUX.png"),
            new Dogs(101L, "Тузик", 4, "хаски", "любит детей", "найден у заброшки", true, false, false, "media/nn-mj-2ENlyHquUX.png"),
            new Dogs(102L, "Бобик", 5, "акита", "любит рыбов", "найден на вокзале", false, false, true, "media/nn-mj-2ENlyHquUX.png")
    );

    @Test
    @DisplayName("Получить всех собак")
    void getAllDogs_ShouldReturnAllDogs() {
        when(dogsRepository.findAll())
                .thenReturn(DOGS_LIST);

        assertIterableEquals(DOGS_LIST, dogService.getAllDogs());
    }

    @Test
    @DisplayName("Создание песика")
    void createDog_ShouldSaveDog() {
        when(dogService.createDog(sharik)).thenReturn(sharik);

        assertEquals(dogService.createDog(sharik), sharik);
    }

    @Test
    @DisplayName("Проверка вызова ошибки при создании песика")
    void createDog_NullDog_ShouldThrowException() {
        assertThrows(DogNotFoundException.class, () -> {
            dogService.createDog(null);
        });
    }

    @Test
    @DisplayName("Обновление песика по ID")
    public void updateDog_ShouldUpdateDog() {
        long id = 10L;

        Dogs updatedDog = tuzik;

        when(dogsRepository.findDogsById(id)).thenReturn(sharik);

        when(dogsRepository.save(any(Dogs.class))).thenReturn(tuzik);
        Dogs result = dogService.updateDog(id, tuzik);

        assertEquals(updatedDog, result);
    }

    @Test
    @DisplayName("Проверка вызова ошибки при обновлении песика")
    void updateDog_NonExistentDog_ShouldThrowException() {

        when(dogsRepository.findDogsById(1L)).thenReturn(null);

        assertThrows(DogNotFoundException.class, () -> {
            dogService.updateDog(1L, sharik);
        });
    }

    @Test
    @DisplayName("Удаление песика по его id")
    void deleteDogById_ShouldDeleteDog() {

        when(dogsRepository.existsById(1L)).thenReturn(true);

        dogService.deleteDogById(1L);

        verify(dogsRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Проверка вызова ошибки при отсутствии собаки в ходе удаления")
    void deleteDogById_NonExistentDog_ShouldThrowException() {
        when(dogsRepository.existsById(1L)).thenReturn(false);

        assertThrows(DogNotFoundException.class, () -> {
            dogService.deleteDogById(1L);
        });
    }

    @Test
    @DisplayName("Поиск собак, нашедших дом")
    void getAllWhoFoundHome_ShouldReturnAllFoundHomeDogs() {

        when(dogsRepository.findDogsByAtHomeIsTrue()).thenReturn(DOGS_LIST);

        List<Dogs> result = dogService.getAllWhoFoundHome();

        assertEquals(DOGS_LIST, result);
        verify(dogsRepository, times(1)).findDogsByAtHomeIsTrue();
    }

    @Test
    @DisplayName("Поиск собак, нуждающихся в кураторах")
    void getAllWhoNeededCurator_ShouldReturnAllDogsNeedingCurator() {

        when(dogsRepository.findDogsByFindCuratorIsTrue()).thenReturn(DOGS_LIST);

        List<Dogs> result = dogService.getAllWhoNeededCurator();

        assertEquals(DOGS_LIST, result);
        verify(dogsRepository, times(1)).findDogsByFindCuratorIsTrue();
    }

    @Test
    @DisplayName("Поиск собак, нуждающихся в хозяине")
    void getAllWhoNeededOwner_ShouldReturnAllDogsNeedingOwner() {

        when(dogsRepository.findDogsByFindOwnerIsTrue()).thenReturn(DOGS_LIST);

        List<Dogs> result = dogService.getAllWhoNeededOwner();

        assertEquals(DOGS_LIST, result);
        verify(dogsRepository, times(1)).findDogsByFindOwnerIsTrue();
    }

}
