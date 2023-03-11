package top.rrricardo.postcalendarbackend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;

import java.util.ArrayList;


@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMapper userMapper;

    @BeforeEach
    public void Setup() {
        var user1 = new User(
                "test1@test.com",
                "test1",
                "test1"
        );

        var user2 = new User(
                "test2@test.com",
                "test2",
                "test2"
        );

        var users = new ArrayList<User>();
        users.add(user1);
        users.add(user2);

        Mockito.when(userMapper.getUsers()).thenReturn(users);

        Mockito.when(userMapper.getUser(1)).thenReturn(user1);
        Mockito.when(userMapper.getUser(2)).thenReturn(user2);
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("test1"));


        mockMvc.perform(MockMvcRequestBuilders.get("/user/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("test2"));
    }

    @Test
    public void getUsersTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("test1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("test2"));
    }
}
