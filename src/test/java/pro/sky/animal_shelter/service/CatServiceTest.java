package pro.sky.animal_shelter.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.animal_shelter.entity.Cats;
import pro.sky.animal_shelter.exception.CatNotFoundException;
import pro.sky.animal_shelter.repository.CatsRepository;
import pro.sky.animal_shelter.service.implementations.CatServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatServiceTest {

    @Mock
    private CatsRepository catsRepository;

    @InjectMocks
    private CatServiceImpl catService;

    Cats vaska = new Cats(1L, "Васька", 1, "беспородная", "игривый", "найден у магазина", true, true, false, "media/nn-mj-2ENlyHquUX.png");
    Cats murca = new Cats(2L, "Мурка", 3, "беспородная", "осторожная", "найден у стройки", true, false, false, "media/nn-mj-2ENlyHquUX.png");

    public static final List<Cats> CATS_LIST = List.of(
            new Cats(1L, "Васька", 1, "беспородная", "игривый", "найден у магазина", true, true, false, "media/nn-mj-2ENlyHquUX.png"),
            new Cats(2L, "Мурка", 3, "беспородная", "осторожная", "найден у стройки", true, false, false, "media/nn-mj-2ENlyHquUX.png"),
            new Cats(3L, "Рыжик", 2, "мейнкун", "тихий", "отдан по причине пропажи владельца", true, false, false, "media/nn-mj-2ENlyHquUX.png")
    );

    @Test
    @DisplayName("Получить всех кошек")
    void getAllCat_ShouldReturnAllCat(){
        when(catsRepository.findAll())
                .thenReturn(CATS_LIST);

        assertIterableEquals(CATS_LIST, catService.getAllCats());
    }
    @Test
    @DisplayName("Создание котика")
    void createCat_ShouldSaveCat() {
        when(catService.createCat(vaska)).thenReturn(vaska);

        assertEquals(catService.createCat(vaska), vaska);
    }

    @Test
    @DisplayName("Проверка вызова ошибки при создании котика")
    void createCat_NullCat_ShouldThrowException() {
        assertThrows(CatNotFoundException.class, () -> {
            catService.createCat(null);
        });
    }

    @Test
    @DisplayName("Обновление котика по ID")
    public void updateCat_ShouldUpdateCat() {
        long id = 2L;

        Cats updatedCat = murca;

        when(catsRepository.findCatsById(id)).thenReturn(vaska);

        when(catsRepository.save(any(Cats.class))).thenReturn(murca);
        Cats result = catService.updateCat(id, murca);

        assertEquals(updatedCat, result);
    }

    @Test
    @DisplayName("Проверка вызова ошибки при обновлении котика")
    void updateCat_NonExistentCat_ShouldThrowException() {

        when(catsRepository.findCatsById(1L)).thenReturn(null);

        assertThrows(CatNotFoundException.class, () -> {
            catService.updateCat(1L, vaska);
        });
    }

    @Test
    @DisplayName("Удаление котика по его id")
    void deleteCatById_ShouldDeleteCat() {

        when(catsRepository.existsById(2L)).thenReturn(true);

        catService.deleteCatById(2L);

        verify(catsRepository, times(1)).deleteById(2L);
    }

    @Test
    @DisplayName("Проверка вызова ошибки при отсутствии котика в ходе удаления")
    void deleteCatById_NonExistentCat_ShouldThrowException() {
        when(catsRepository.existsById(1L)).thenReturn(false);

        assertThrows(CatNotFoundException.class, () -> {
            catService.deleteCatById(1L);
        });
    }

    @Test
    @DisplayName("Поиск котиков, нашедших дом")
    void getAllWhoFoundHome_ShouldReturnAllFoundHomeCats() {

        when(catsRepository.findCatsByAtHomeIsTrue()).thenReturn(CATS_LIST);

        List<Cats> result = catService.getAllWhoFoundHome();

        assertEquals(CATS_LIST, result);
        verify(catsRepository, times(1)).findCatsByAtHomeIsTrue();
    }

    @Test
    @DisplayName("Поиск котиков, нуждающихся в кураторах")
    void getAllWhoNeededCurator_ShouldReturnAllCatNeedingCurator() {

        when(catsRepository.findCatsByFindCuratorIsTrue()).thenReturn(CATS_LIST);

        List<Cats> result = catService.getAllWhoNeededCurator();

        assertEquals(CATS_LIST, result);
        verify(catsRepository, times(1)).findCatsByFindCuratorIsTrue();
    }

    @Test
    @DisplayName("Поиск котиков, нуждающихся в хозяине")
    void getAllWhoNeededOwner_ShouldReturnAllCatNeedingOwner() {

        when(catsRepository.findCatsByFindOwnerIsTrue()).thenReturn(CATS_LIST);

        List<Cats> result = catService.getAllWhoNeededOwner();

        assertEquals(CATS_LIST, result);
        verify(catsRepository, times(1)).findCatsByFindOwnerIsTrue();
    }


}
