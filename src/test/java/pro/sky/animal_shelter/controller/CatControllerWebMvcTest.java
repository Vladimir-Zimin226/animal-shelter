package pro.sky.animal_shelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.animal_shelter.entity.Cats;
import pro.sky.animal_shelter.service.implementations.CatServiceImpl;

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

@WebMvcTest(CatController.class)
public class CatControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatServiceImpl catService;

    @Test
    public void testCreateCat() throws Exception {
        Cats testCat = new Cats();
        given(catService.createCat(any(Cats.class))).willReturn(testCat);

        mockMvc.perform(post("/cats/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testCat)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCat() throws Exception {
        Cats testCat = new Cats();
        given(catService.updateCat(anyLong(), any(Cats.class))).willReturn(testCat);

        mockMvc.perform(put("/cats/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testCat)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllCats() throws Exception {
        List<Cats> catsList = Arrays.asList(new Cats(), new Cats());
        given(catService.getAllCats()).willReturn(catsList);

        mockMvc.perform(get("/cats/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeleteCatById() throws Exception {
        mockMvc.perform(delete("/cats/delete/1"))
                .andExpect(status().isOk());

        verify(catService).deleteCatById(1L);
    }

    @Test
    public void testGetAllCatsWhoNeededOwner() throws Exception {
        List<Cats> catsList = Arrays.asList(new Cats(), new Cats());
        given(catService.getAllWhoNeededOwner()).willReturn(catsList);

        mockMvc.perform(get("/cats/all_cats_who_need_owner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    public void testGetAllCatsWhoNeededCurator() throws Exception {
        List<Cats> catsList = Arrays.asList(new Cats(), new Cats());
        given(catService.getAllWhoNeededCurator()).willReturn(catsList);

        mockMvc.perform(get("/cats/all_cats_who_need_curator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    public void testGetAllCatsWhoFoundHome() throws Exception {
        List<Cats> catsList = Arrays.asList(new Cats(), new Cats());
        given(catService.getAllWhoFoundHome()).willReturn(catsList);

        mockMvc.perform(get("/cats/all_cats_who_found_home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }
}
