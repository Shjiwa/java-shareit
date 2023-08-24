package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.modelFactory.ModelFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private final ModelFactory factory = ModelFactory.getInstance();

    @Test
    void shouldCreateTest() throws Exception {
        Long userId = 1L;

        ItemDto requestDto = factory.getItemDto();
        ItemDto responseDto = getItemResponseDto(1L);

        when(itemService.addItem(any(ItemDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(OWNER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).addItem(any(ItemDto.class), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void shouldCreateCommentTest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentDto requestDto = new CommentDto();
        requestDto.setText("Comment");

        CommentDto responseDto = new CommentDto();
        responseDto.setId(1L);

        when(itemService.addComment(any(CommentDto.class), eq(itemId), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(OWNER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1))
                .addComment(any(CommentDto.class), eq(itemId), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void shouldGetByIdTest() throws Exception {
        Long userId = 1L;

        ItemDto responseDto = getItemResponseDto(10L);

        when(itemService.getItemInfo(eq(responseDto.getId()), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(get("/items/" + responseDto.getId())
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).getItemInfo(eq(responseDto.getId()), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void shouldGetAllByOwnerIdTest() throws Exception {
        Long userId = 1L;

        ItemDto responseDto1 = getItemResponseDto(10L);
        ItemDto responseDto2 = getItemResponseDto(11L);

        List<ItemDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(itemService.getOwnerItems(eq(userId))).thenReturn(responseDtoList);

        mockMvc.perform(get("/items")
                        .header(OWNER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(itemService, times(1)).getOwnerItems(eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void shouldGetAllBySearchTextTest() throws Exception {
        ItemDto responseDto1 = getItemResponseDto(10L);
        ItemDto responseDto2 = getItemResponseDto(11L);

        List<ItemDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(itemService.getItemsByKeyword(anyString())).thenReturn(responseDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", "someText"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(itemService, times(1)).getItemsByKeyword(eq("someText"));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void shouldUpdateTest() throws Exception {
        Long userId = 1L;
        Long itemId = 10L;

        ItemDto requestDto = factory.getItemDto();
        ItemDto responseDto = getItemResponseDto(10L);

        when(itemService.update(eq(itemId), eq(userId), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(OWNER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).update(eq(itemId), eq(userId), any(ItemDto.class));
        verifyNoMoreInteractions(itemService);
    }

    private ItemDto getItemResponseDto(Long id) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        return itemDto;
    }
}