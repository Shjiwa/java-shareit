package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemResponseWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.OWNER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void shouldCreateTest() throws Exception {
        Long userId = 1L;

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test item request");

        ItemResponseDto responseDto = ItemResponseDto.builder()
                .id(1L)
                .build();

        when(itemRequestService.create(eq(userId), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(OWNER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemRequestService, times(1)).create(eq(userId), any(ItemRequestDto.class));
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void shouldGetAllByOwnerIdTest() throws Exception {
        Long userId = 1L;

        ItemResponseWithItemsDto responseDto1 = getResponseDto(1L);
        ItemResponseWithItemsDto responseDto2 = getResponseDto(2L);

        Collection<ItemResponseWithItemsDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(itemRequestService.getAllByRequesterId(eq(userId), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/requests")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(itemRequestService, times(1)).getAllByRequesterId(eq(userId), anyInt(), anyInt());
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void shouldGetAllTest() throws Exception {
        Long userId = 1L;

        ItemResponseWithItemsDto responseDto1 = getResponseDto(1L);
        ItemResponseWithItemsDto responseDto2 = getResponseDto(2L);

        List<ItemResponseWithItemsDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(itemRequestService.getAll(eq(userId), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/requests/all")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(itemRequestService, times(1)).getAll(eq(userId), anyInt(), anyInt());
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void shouldGetByIdTest() throws Exception {
        Long userId = 1L;

        ItemResponseWithItemsDto responseDto = getResponseDto(1L);

        when(itemRequestService.getById(eq(userId), eq(responseDto.getId()))).thenReturn(responseDto);

        mockMvc.perform(get("/requests/" + responseDto.getId())
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemRequestService, times(1)).getById(eq(userId), eq(responseDto.getId()));
        verifyNoMoreInteractions(itemRequestService);
    }

    private ItemResponseWithItemsDto getResponseDto(Long id) {
        return ItemResponseWithItemsDto.builder()
                .id(id)
                .build();
    }
}