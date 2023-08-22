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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.*;

@Slf4j
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void getByIdTest() throws Exception {
        Long userId = 1L;

        BookingDtoOut responseDto = getBookingResponseDto(10L);

        when(bookingService.getBookingById(eq(userId), eq(responseDto.getId()))).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/" + responseDto.getId())
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).getBookingById(eq(userId), eq(responseDto.getId()));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllByStateTest() throws Exception {
        Long userId = 1L;

        BookingDtoOut responseDto1 = getBookingResponseDto(10L);
        BookingDtoOut responseDto2 = getBookingResponseDto(11L);

        List<BookingDtoOut> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(bookingService.getAllBookingsByUser(eq(userId), any(), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(bookingService, times(1)).getAllBookingsByUser(eq(userId), eq(State.ALL), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllByStateForOwnerTest() throws Exception {
        Long userId = 1L;

        BookingDtoOut responseDto1 = getBookingResponseDto(10L);
        BookingDtoOut responseDto2 = getBookingResponseDto(11L);

        List<BookingDtoOut> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(bookingService.getAllBookingsForAllItemsByOwner(eq(userId), any(), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(bookingService, times(1)).getAllBookingsForAllItemsByOwner(eq(userId), eq(State.ALL), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createTest() throws Exception {
        Long userId = 1L;

        BookingDtoIn requestDto = BookingDtoIn.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(20))
                .itemId(1L)
                .build();

        BookingDtoOut responseDto = getBookingResponseDto(10L);

        when(bookingService.addBooking(any(BookingDtoIn.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header(OWNER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).addBooking(any(BookingDtoIn.class), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveTest() throws Exception {
        Long userId = 1L;

        BookingDtoOut responseDto = getBookingResponseDto(10L);

        when(bookingService.updateBooking(eq(responseDto.getId()), anyBoolean(), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/" + responseDto.getId())
                        .header(OWNER_ID_HEADER, userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).updateBooking(eq(responseDto.getId()), eq(false), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    private BookingDtoOut getBookingResponseDto(Long id) {
        return BookingDtoOut.builder()
                .id(id)
                .build();
    }
}