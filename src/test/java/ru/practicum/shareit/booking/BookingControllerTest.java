package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.State;
import ru.practicum.shareit.modelFactory.ModelFactory;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@Slf4j
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private final ModelFactory factory = ModelFactory.getInstance();

    @Test
    void shouldCreateTest() throws Exception {
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.now();
        BookingDtoIn requestDto = factory.getBookingDtoIn(time);
        requestDto.setItemId(1L);

        BookingDtoOut responseDto = factory.getBookingResponseDto(1L, time);

        when(bookingService.addBooking(any(BookingDtoIn.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(OWNER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1))
                .addBooking(any(BookingDtoIn.class), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void shouldGetByIdTest() throws Exception {
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.now();
        BookingDtoOut responseDto = factory.getBookingResponseDto(1L, time);

        when(bookingService.getBookingById(eq(userId), eq(responseDto.getId()))).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/" + responseDto.getId())
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1))
                .getBookingById(eq(userId), eq(responseDto.getId()));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void shouldGetAllByStateTest() throws Exception {
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.now();
        BookingDtoOut responseDto1 = factory.getBookingResponseDto(1L, time);
        BookingDtoOut responseDto2 = factory.getBookingResponseDto(2L, time);

        List<BookingDtoOut> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(bookingService.getAllBookingsByUser(eq(userId), any(), anyInt(), anyInt()))
                .thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(bookingService, times(1))
                .getAllBookingsByUser(eq(userId), eq(State.ALL), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void shouldGetAllByStateForOwnerTest() throws Exception {
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.now();
        BookingDtoOut responseDto1 = factory.getBookingResponseDto(1L, time);
        BookingDtoOut responseDto2 = factory.getBookingResponseDto(2L, time);

        List<BookingDtoOut> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(bookingService.getAllBookingsForAllItemsByOwner(eq(userId), any(), anyInt(), anyInt()))
                .thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(bookingService, times(1))
                .getAllBookingsForAllItemsByOwner(eq(userId), eq(State.ALL), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void shouldUpdateBookingTest() throws Exception {
        Long userId = 1L;
        LocalDateTime time = LocalDateTime.now();
        BookingDtoOut responseDto = factory.getBookingResponseDto(1L, time);

        when(bookingService.updateBooking(eq(responseDto.getId()), anyBoolean(), eq(userId)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/" + responseDto.getId())
                        .header(OWNER_ID_HEADER, userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1))
                .updateBooking(eq(responseDto.getId()), eq(false), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void shouldGetInternal() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(OWNER_ID_HEADER, 1)
                        .param("from", "-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldGetMismatch() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(OWNER_ID_HEADER, 1)
                        .param("state", "LOL")
                        .param("from", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}