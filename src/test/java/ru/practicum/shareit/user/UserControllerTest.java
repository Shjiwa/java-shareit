package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.modelFactory.ModelFactory;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    private final ModelFactory factory = ModelFactory.getInstance();

    @Test
    void createTest() throws Exception {
        UserDto userDto = factory.getUserDto();

        when(userService.add(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService, times(1)).add(any(UserDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getByIdTest() throws Exception {
        UserDto userDto = factory.getUserDto();
        userDto.setId(1L);

        when(userService.getById(eq(userDto.getId()))).thenReturn(userDto);

        mockMvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService, times(1)).getById(eq(userDto.getId()));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void getAllTest() throws Exception {
        UserDto userDto1 = factory.getUserDto();
        UserDto userDto2 = factory.getUserDto();
        userDto2.setId(2L);

        List<UserDto> userDtoList = Arrays.asList(
                userDto1,
                userDto2
        );

        when(userService.getAllUsers()).thenReturn(userDtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(userDto2.getId()));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updateTest() throws Exception {
        UserDto userDto = factory.getUserDto();
        userDto.setId(1L);

        when(userService.update(eq(userDto.getId()), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/" + userDto.getId())
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));

        verify(userService, times(1)).update(eq(userDto.getId()), any(UserDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(eq(1L));
        verifyNoMoreInteractions(userService);
    }
}