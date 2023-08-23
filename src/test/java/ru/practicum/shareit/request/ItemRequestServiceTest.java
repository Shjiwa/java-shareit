package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.modelFactory.ModelFactory;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemResponseWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final ModelFactory factory = ModelFactory.getInstance();

    @Test
    void getAllByRequesterIdTest() {
        User owner = factory.getUser(1L);
        User requester = factory.getUser(2L);

        ItemRequest itemRequest1 = getItemRequest(10L);
        itemRequest1.setRequester(requester);

        ItemRequest itemRequest2 = getItemRequest(11L);
        itemRequest2.setRequester(requester);

        Item item1 = factory.getItem(100L, owner);
        item1.setRequest(itemRequest1);

        Item item2 = factory.getItem(101L, owner);
        item2.setRequest(itemRequest2);

        List<ItemRequest> itemRequestList = Arrays.asList(
                itemRequest1,
                itemRequest2
        );

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreated(eq(requester.getId()), any(Pageable.class)))
                .thenReturn(itemRequestList);
        when(itemRepository.findAllByRequestRequesterIdIn(anyList())).thenReturn(List.of(item1));

        List<ItemResponseWithItemsDto> resultDtoList = (List<ItemResponseWithItemsDto>) itemRequestService
                .getAllByRequesterId(requester.getId(), 0, 10);

        assertThat(resultDtoList.size(), equalTo(2));

        assertThat(resultDtoList.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(resultDtoList.get(0).getItems().get(0).getRequestId(), equalTo(item1.getRequest().getId()));

        assertThat(resultDtoList.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(resultDtoList.get(1).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(1).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(1).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(1).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(1).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(resultDtoList.get(1).getItems().get(0).getRequestId(), equalTo(item1.getRequest().getId()));

        verify(userRepository, times(1)).findById(eq(requester.getId()));
        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdOrderByCreated(eq(requester.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByRequestRequesterIdIn(anyList());
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void getAllTest() {
        User owner = factory.getUser(1L);
        User requester = factory.getUser(2L);

        ItemRequest itemRequest1 = getItemRequest(10L);
        itemRequest1.setRequester(requester);

        ItemRequest itemRequest2 = getItemRequest(11L);
        itemRequest2.setRequester(requester);

        Item item1 = factory.getItem(100L, owner);
        item1.setRequest(itemRequest1);

        Item item2 = factory.getItem(101L, owner);
        item2.setRequest(itemRequest2);

        List<ItemRequest> itemRequestList = Arrays.asList(
                itemRequest1,
                itemRequest2
        );

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findAllByRequesterIdNot(eq(owner.getId()), any(Pageable.class)))
                .thenReturn(itemRequestList);
        when(itemRepository.findAllByRequestRequesterIdIn(anyList())).thenReturn(List.of(item1));

        List<ItemResponseWithItemsDto> resultDtoList = (List<ItemResponseWithItemsDto>) itemRequestService
                .getAll(owner.getId(), 0, 10);

        assertThat(resultDtoList.size(), equalTo(2));

        assertThat(resultDtoList.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(resultDtoList.get(0).getItems().get(0).getRequestId(), equalTo(item1.getRequest().getId()));

        assertThat(resultDtoList.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(resultDtoList.get(1).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(1).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(1).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(1).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(1).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(resultDtoList.get(1).getItems().get(0).getRequestId(), equalTo(item1.getRequest().getId()));

        verify(itemRequestRepository, times(1))
                .findAllByRequesterIdNot(eq(owner.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByRequestRequesterIdIn(anyList());
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void getByIdTest() {
        User owner = factory.getUser(1L);
        User requester = factory.getUser(2L);

        ItemRequest itemRequest = getItemRequest(10L);
        itemRequest.setRequester(requester);

        Item item = factory.getItem(100L, owner);
        item.setRequest(itemRequest);

        when(userRepository.findById(eq(requester.getId()))).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(eq(itemRequest.getId()))).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(eq(itemRequest.getId()))).thenReturn(List.of(item));

        ItemResponseWithItemsDto resultDto = itemRequestService.getById(requester.getId(), itemRequest.getId());

        assertThat(resultDto.getId(), equalTo(itemRequest.getId()));
        assertThat(resultDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(resultDto.getItems().size(), equalTo(1));
        assertThat(resultDto.getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(resultDto.getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(resultDto.getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultDto.getItems().get(0).getRequestId(), equalTo(item.getRequest().getId()));

        verify(userRepository, times(1)).findById(eq(requester.getId()));
        verify(itemRequestRepository, times(1)).findById(eq(itemRequest.getId()));
        verify(itemRepository, times(1)).findAllByRequestId(eq(itemRequest.getId()));
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void createTest() {
        User user = factory.getUser(1L);
        ItemRequest itemRequest = getItemRequest(10L);

        ItemRequestDto itemRequestCreateDto = new ItemRequestDto();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemResponseDto resultDto = itemRequestService.create(user.getId(), itemRequestCreateDto);

        assertThat(resultDto.getId(), equalTo(itemRequest.getId()));
        assertThat(resultDto.getDescription(), equalTo(itemRequest.getDescription()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    private ItemRequest getItemRequest(Long id) {
        ItemRequest request = new ItemRequest();
        request.setId(id);
        request.setDescription("Request" + id);
        return request;
    }
}