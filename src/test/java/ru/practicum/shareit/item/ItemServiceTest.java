package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.constant.Status.APPROVED;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void createTest() {
        User user = getUser(1L);

        ItemRequest itemRequest = getItemRequest();

        Item item = getItem(100L);
        item.setOwner(user);
        item.setRequest(itemRequest);

        ItemDto createDto = new ItemDto();
        createDto.setId(item.getId());
        createDto.setName(item.getName());
        createDto.setDescription(item.getDescription());
        createDto.setAvailable(item.getAvailable());
        createDto.setRequestId(itemRequest.getId());

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto resultDto = itemService.addItem(createDto, user.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultDto.getRequestId(), equalTo(itemRequest.getId()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(itemRequestRepository, times(1)).findById(eq(itemRequest.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void createCommentTest() {
        User user = getUser(1L);
        Item item = getItem(10L);

        Comment comment = getComment(100L);
        comment.setItem(item);
        comment.setAuthor(user);

        CommentDto createDto = CommentDto.builder()
                .text(comment.getText())
                .authorName(user.getName())
                .build();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByBooker_IdAndItem_IdAndStartIsBeforeAndEndIsBefore(eq(user.getId()), eq(item.getId()), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto resultDto = itemService.addComment(createDto, item.getId(), user.getId());

        assertThat(resultDto.getId(), equalTo(comment.getId()));
        assertThat(resultDto.getText(), equalTo(comment.getText()));
        assertThat(resultDto.getAuthorName(), equalTo(user.getName()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findAllByBooker_IdAndItem_IdAndStartIsBeforeAndEndIsBefore(eq(user.getId()), eq(item.getId()), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void createCommentTest_NoBookings() {
        User user = getUser(1L);
        Item item = getItem(10L);

        CommentDto commentDto = CommentDto.builder()
                .text("Comment text " + 100L)
                .authorName(user.getName())
                .build();

        when(bookingRepository.findAllByBooker_IdAndItem_IdAndStartIsBeforeAndEndIsBefore(eq(user.getId()), eq(item.getId()), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        BadRequestException e = assertThrows(BadRequestException.class, () ->
                itemService.addComment(commentDto, item.getId(), user.getId()));

        assertThat(e.getMessage(), equalTo("Error! A review can only be left by the user who rented this item, " +
                "and only after the end of the rental period."));

        verify(bookingRepository, times(1)).findAllByBooker_IdAndItem_IdAndStartIsBeforeAndEndIsBefore(eq(user.getId()), eq(item.getId()), any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getByIdAsOwnerTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item = getItem(10L);
        item.setOwner(owner);

        Booking lastBooking = getBooking(100L, booker, item);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking nextBooking = getBooking(101L, booker, item);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        Comment comment1 = getComment(1000L);
        comment1.setItem(item);
        comment1.setAuthor(booker);

        Comment comment2 = getComment(1001L);
        comment2.setItem(item);
        comment2.setAuthor(booker);

        List<Comment> commentList = Arrays.asList(
                comment1,
                comment2
        );

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(eq(item.getId()), eq(APPROVED), any(LocalDateTime.class), any(Sort.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartIsAfter(eq(item.getId()), eq(APPROVED), any(LocalDateTime.class), any(Sort.class))).thenReturn(Optional.of(nextBooking));
        when(commentRepository.findAllByItemId(eq(item.getId()))).thenReturn(commentList);

        ItemDtoOwner resultDto = (ItemDtoOwner) itemService.getItemInfo(item.getId(), owner.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));

        assertThat(resultDto.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(resultDto.getNextBooking().getBookerId(), equalTo(booker.getId()));
        assertThat(resultDto.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(resultDto.getLastBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(resultDto.getComments().size(), equalTo(2));
        assertThat(resultDto.getComments().get(0).getId(), equalTo(comment1.getId()));
        assertThat(resultDto.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(resultDto.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(resultDto.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(resultDto.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(resultDto.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).findFirstByItemIdAndStatusAndStartIsBefore(eq(item.getId()), eq(APPROVED), any(LocalDateTime.class), any(Sort.class));
        verify(bookingRepository, times(1)).findFirstByItemIdAndStatusAndStartIsAfter(eq(item.getId()), eq(APPROVED), any(LocalDateTime.class), any(Sort.class));
        verify(commentRepository, times(1)).findAllByItemId(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getByIdAsNotOwnerTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);
        User notOwner = getUser(3L);

        Item item = getItem(10L);
        item.setOwner(owner);

        Booking lastBooking = getBooking(100L, booker, item);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking nextBooking = getBooking(101L, booker, item);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        List<Booking> bookingList = Arrays.asList(
                lastBooking,
                nextBooking
        );

        Comment comment1 = getComment(1000L);
        comment1.setAuthor(booker);

        Comment comment2 = getComment(1001L);
        comment2.setAuthor(booker);

        List<Comment> commentList = Arrays.asList(
                comment1,
                comment2
        );

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemId(eq(item.getId()))).thenReturn(commentList);

        ItemDtoOwner resultDto = (ItemDtoOwner) itemService.getItemInfo(item.getId(), notOwner.getId());

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));

        assertThat(resultDto.getNextBooking(), equalTo(null));
        assertThat(resultDto.getLastBooking(), equalTo(null));

        assertThat(resultDto.getComments().size(), equalTo(2));
        assertThat(resultDto.getComments().get(0).getId(), equalTo(comment1.getId()));
        assertThat(resultDto.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(resultDto.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(resultDto.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(resultDto.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(resultDto.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(commentRepository, times(1)).findAllByItemId(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getAllByOwnerIdTest() {
        User owner = getUser(1L);
        User booker = getUser(2L);

        Item item1 = getItem(10L);
        item1.setOwner(owner);

        Item item2 = getItem(11L);
        item2.setOwner(owner);

        List<Item> itemList = Arrays.asList(
                item1,
                item2
        );

        Booking item1lastBooking = getBooking(100L, booker, item1);
        item1lastBooking.setStart(LocalDateTime.now().minusDays(2));
        item1lastBooking.setEnd(LocalDateTime.now().minusDays(1));

        Booking item1nextBooking = getBooking(101L, booker, item1);
        item1nextBooking.setStart(LocalDateTime.now().plusDays(1));
        item1nextBooking.setEnd(LocalDateTime.now().plusDays(2));

        List<Booking> item1bookingList = Arrays.asList(
                item1lastBooking,
                item1nextBooking
        );

        Comment comment1 = getComment(1000L);
        comment1.setItem(item1);
        comment1.setAuthor(booker);

        Comment comment2 = getComment(1001L);
        comment2.setItem(item1);
        comment2.setAuthor(booker);

        List<Comment> item1commentList = Arrays.asList(
                comment1,
                comment2
        );

        when(itemRepository.findAllByOwnerId(eq(owner.getId()), any(Sort.class))).thenReturn(itemList);
        when(bookingRepository.findAllByItem_IdIn(anyList())).thenReturn(item1bookingList);
        //when(bookingRepository.findAllByItem_IdIn(anyList())).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItem_IdIn(anyList())).thenReturn(item1commentList);
        //when(commentRepository.findAllByItem_IdIn(anyList())).thenReturn(new ArrayList<>());

        List<ItemDto> resultDtoList = new ArrayList<>(itemService.getOwnerItems(owner.getId()));
        ItemDtoOwner item11 = (ItemDtoOwner) resultDtoList.get(0);
        ItemDtoOwner item22 = (ItemDtoOwner) resultDtoList.get(1);

        assertThat(resultDtoList.size(), equalTo(2));
        assertThat(item11.getId(), equalTo(item11.getId()));
        assertThat(item11.getName(), equalTo(item11.getName()));
        assertThat(item11.getDescription(), equalTo(item11.getDescription()));
        assertThat(item11.getAvailable(), equalTo(item11.getAvailable()));

        assertThat(item11.getNextBooking().getId(), equalTo(item1nextBooking.getId()));
        assertThat(item11.getNextBooking().getBookerId(), equalTo(booker.getId()));
        assertThat(item11.getLastBooking().getId(), equalTo(item1lastBooking.getId()));
        assertThat(item11.getLastBooking().getBookerId(), equalTo(booker.getId()));

        assertThat(item11.getComments().size(), equalTo(2));
        assertThat(item11.getComments().get(0).getId(), equalTo(comment1.getId()));
        assertThat(item11.getComments().get(0).getText(), equalTo(comment1.getText()));
        assertThat(item11.getComments().get(0).getAuthorName(), equalTo(booker.getName()));
        assertThat(item11.getComments().get(1).getId(), equalTo(comment2.getId()));
        assertThat(item11.getComments().get(1).getText(), equalTo(comment2.getText()));
        assertThat(item11.getComments().get(1).getAuthorName(), equalTo(booker.getName()));

        assertThat(resultDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(resultDtoList.get(1).getName(), equalTo(item2.getName()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultDtoList.get(1).getAvailable(), equalTo(item2.getAvailable()));

        assertThat(item22.getNextBooking(), equalTo(null));
        assertThat(item22.getLastBooking(), equalTo(null));

        assertThat(item22.getComments().size(), equalTo(0));

        verify(itemRepository, times(1)).findAllByOwnerId(eq(owner.getId()), any(Sort.class));
        verify(bookingRepository, times(1)).findAllByItem_IdIn(anyList());
        verify(commentRepository, times(1)).findAllByItem_IdIn(anyList());
        verifyNoMoreInteractions(itemRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getAllBySearchTextTest() {
        String searchText = "Item";

        Item item1 = getItem(1L);
        Item item2 = getItem(2L);

        List<Item> itemList = Arrays.asList(
                item1,
                item2
        );

        when(itemRepository.search(eq(searchText))).thenReturn(itemList);

        List<ItemDto> resultDtoList = new ArrayList<>(itemService.getItemsByKeyword(searchText));

        assertThat(resultDtoList.size(), equalTo(2));

        assertThat(resultDtoList.get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getAvailable(), equalTo(item1.getAvailable()));

        assertThat(resultDtoList.get(1).getId(), equalTo(item2.getId()));
        assertThat(resultDtoList.get(1).getName(), equalTo(item2.getName()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultDtoList.get(1).getAvailable(), equalTo(item2.getAvailable()));

        verify(itemRepository, times(1)).search(eq(searchText));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void getAllBySearchTextTest_BlankQuery() {
        Collection<ItemDto> resultDtoList = itemService.getItemsByKeyword(" ");

        assertThat(resultDtoList.size(), equalTo(0));

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void updateTest() {
        ItemDto inputDto = new ItemDto();

        User owner = getUser(1L);

        Item item = getItem(10L);
        item.setOwner(owner);

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));

        ItemDto resultDto = itemService.update(item.getId(), owner.getId(), inputDto);

        assertThat(resultDto.getId(), equalTo(item.getId()));
        assertThat(resultDto.getName(), equalTo(item.getName()));
        assertThat(resultDto.getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getAvailable(), equalTo(item.getAvailable()));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void updateTest_NotOwner() {
        ItemDto inputDto = new ItemDto();

        User owner = getUser(1L);
        User notOwner = getUser(2L);

        Item item = getItem(10L);
        item.setOwner(owner);

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(eq(notOwner.getId()))).thenReturn(Optional.ofNullable(notOwner));

        ForbiddenException e = assertThrows(ForbiddenException.class, () -> {
            itemService.update(item.getId(), notOwner.getId(), inputDto);
        });

        assertThat(e.getMessage(), equalTo("Error! You don't have permission to access this option." +
                " Only the owner of the item can update it."));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void updateTest_InvalidDto() {
        ItemDto inputDto = new ItemDto();
        inputDto.setName("");

        User owner = getUser(1L);

        Item item = getItem(10L);
        item.setOwner(owner);

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            itemService.update(item.getId(), owner.getId(), inputDto);
        });

        assertThat(e.getMessage(), equalTo("User not found."));

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateTest_Conflict() {
        ItemDto inputDto = new ItemDto();

        User owner = getUser(1L);

        Item item = getItem(10L);
        item.setOwner(owner);

        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.empty());
//        when(itemRepository.save(any(Item.class))).thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));

        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            itemService.update(item.getId(), owner.getId(), inputDto);
        });

        assertThat(e.getMessage(), equalTo("Item not found."));

        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository);
    }

    private User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@user.com");
        return user;
    }

    private Item getItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("ItemDescr " + id);
        item.setAvailable(true);
        return item;
    }

    private Comment getComment(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("Comment text " + id);
        return comment;
    }

    private ItemRequest getItemRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(10L);
        request.setDescription("Request " + 10L);
        return request;
    }

    private Booking getBooking(Long id, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        booking.setItem(item);
        return booking;
    }
}