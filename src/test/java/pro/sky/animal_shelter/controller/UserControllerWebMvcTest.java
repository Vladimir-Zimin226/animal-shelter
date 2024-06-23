package pro.sky.animal_shelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.animal_shelter.entity.Users;
import pro.sky.animal_shelter.service.implementations.UserServiceImpl;

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

@WebMvcTest(UserController.class)
public class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Test
    public void testCreateUser() throws Exception {
        Users testUser = new Users();
        given(userService.createUser(any(Users.class))).willReturn(testUser);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        Users testUsers = new Users();
        given(userService.updateUser(anyLong(), any(Users.class))).willReturn(testUsers);

        mockMvc.perform(put("/users/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testUsers)))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetAllUserVolunteer() throws Exception {
        List<Users> userList = Arrays.asList(new Users(), new Users());
        given(userService.getAllVolunteer()).willReturn(userList);

        mockMvc.perform(get("/users/all_volunteer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    public void testGetAllUser() throws Exception {
        List<Users> usersList = Arrays.asList(new Users(), new Users());
        given(userService.getAllUsers()).willReturn(usersList);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
    @Test
    public void testDeleteUserById() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(1L);
    }


}
