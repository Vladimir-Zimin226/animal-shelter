package pro.sky.animal_shelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.animal_shelter.entity.Dogs;
import pro.sky.animal_shelter.service.implementations.DogServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DogController.class)
public class DogControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogServiceImpl dogService;

    @Test
    public void testCreateDog() throws Exception {
        Dogs testDog = new Dogs();
        given(dogService.createDog(any(Dogs.class))).willReturn(testDog);

        mockMvc.perform(post("/dogs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testDog)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateDog() throws Exception {
        Dogs testDog = new Dogs();
        given(dogService.updateDog(anyLong(), any(Dogs.class))).willReturn(testDog);

        mockMvc.perform(put("/dogs/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testDog)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllDogs() throws Exception {
        List<Dogs> dogsList = Arrays.asList(new Dogs(), new Dogs());
        given(dogService.getAllDogs()).willReturn(dogsList);

        mockMvc.perform(get("/dogs/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeleteDogById() throws Exception {
        mockMvc.perform(delete("/dogs/delete/1"))
                .andExpect(status().isOk());

        verify(dogService).deleteDogById(1L);
    }

    @Test
    public void testGetAllDogsWhoNeededOwner() throws Exception {
        List<Dogs> dogsList = Arrays.asList(new Dogs(), new Dogs());
        given(dogService.getAllWhoNeededOwner()).willReturn(dogsList);

        mockMvc.perform(get("/dogs/all_dogs_who_need_owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    public void testGetAllDogsWhoNeededCurator() throws Exception {
        List<Dogs> dogsList = Arrays.asList(new Dogs(), new Dogs());
        given(dogService.getAllWhoNeededCurator()).willReturn(dogsList);

        mockMvc.perform(get("/dogs/all_dogs_who_need_curator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    public void testGetAllDogsWhoFoundHome() throws Exception {
        List<Dogs> dogsList = Arrays.asList(new Dogs(), new Dogs());
        given(dogService.getAllWhoFoundHome()).willReturn(dogsList);

        mockMvc.perform(get("/dogs/all_dogs_who_found_home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }
}
