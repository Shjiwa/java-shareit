package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.error.BadRequestException;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.modelFactory.ModelFactory;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private final ModelFactory factory = ModelFactory.getInstance();

    @Test
    void shouldCreateTest() {
        UserDto inputDto = factory.getUserDto();

        User user = factory.getUser(1L);

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto resultDto = userService.add(inputDto);

        assertThat(resultDto.getId(), equalTo(user.getId()));
        assertThat(resultDto.getName(), equalTo(user.getName()));
        assertThat(resultDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldCreateTestBadRequest() {
        UserDto inputDto = new UserDto();

        BadRequestException e = assertThrows(BadRequestException.class, () ->
                userService.add(inputDto));

        assertThat(e.getMessage(), equalTo("Data is not valid."));

        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldGetByIdTest() {
        User user = factory.getUser(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto resultDto = userService.getById(user.getId());

        assertThat(resultDto.getId(), equalTo(user.getId()));
        assertThat(resultDto.getName(), equalTo(user.getName()));
        assertThat(resultDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldGetAllTest() {
        User user1 = factory.getUser(1L);
        User user2 = factory.getUser(2L);

        List<User> userList = Arrays.asList(
                user1,
                user2
        );

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> resultDtoList = (List<UserDto>) userService.getAllUsers();

        assertThat(resultDtoList.get(0).getId(), equalTo(user1.getId()));
        assertThat(resultDtoList.get(0).getName(), equalTo(user1.getName()));
        assertThat(resultDtoList.get(0).getEmail(), equalTo(user1.getEmail()));

        assertThat(resultDtoList.get(1).getId(), equalTo(user2.getId()));
        assertThat(resultDtoList.get(1).getName(), equalTo(user2.getName()));
        assertThat(resultDtoList.get(1).getEmail(), equalTo(user2.getEmail()));

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldUpdateTest() {
        UserDto inputDto = new UserDto();

        User user = factory.getUser(1L);

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto resultDto = userService.update(user.getId(), inputDto);

        assertThat(resultDto.getId(), equalTo(user.getId()));
        assertThat(resultDto.getName(), equalTo(user.getName()));
        assertThat(resultDto.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldUpdateTestInvalidDto() {
        UserDto inputDto = new UserDto();
        inputDto.setEmail("invalidEmail");

        User user = factory.getUser(1L);

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        BadRequestException e = assertThrows(BadRequestException.class, () ->
                userService.update(user.getId(), inputDto));

        assertThat(e.getMessage(), equalTo("Invalid data to update."));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldUpdateTestConflict() {
        UserDto inputDto = new UserDto();

        User user = factory.getUser(1L);

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));

        ConflictException e = assertThrows(ConflictException.class, () ->
                userService.update(user.getId(), inputDto));

        assertThat(e.getMessage(), equalTo("DataIntegrityViolationException"));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldDeleteTest() {
        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(eq(1L));
        verifyNoMoreInteractions(userRepository);
    }
}