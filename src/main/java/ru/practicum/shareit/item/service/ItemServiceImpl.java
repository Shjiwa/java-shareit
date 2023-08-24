package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constant.Status.APPROVED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
        Item item = ItemMapper.INSTANCE.toItem(itemDto, owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found."));
            item.setRequest(itemRequest);
        }
        itemDto = ItemMapper.INSTANCE.toItemDto(itemRepository.save(item));
        log.info("Success! Item successfully added!");
        return itemDto;
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        LocalDateTime time = LocalDateTime.now();
        if (!bookingRepository
                .findAllByBooker_IdAndItem_IdAndStartIsBeforeAndEndIsBefore(userId, itemId, time, time).isEmpty()) {
            Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
            User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
            Comment comment = CommentMapper.INSTANCE.toComment(commentDto, item, author);
            comment.setCreated(time);
            Comment added = commentRepository.save(comment);
            log.info("Success! Comment successfully added!");
            return CommentMapper.INSTANCE.toCommentDto(added);
        } else {
            throw new BadRequestException("Error! A review can only be left by the user who rented this item, " +
                    "and only after the end of the rental period.");
        }
    }

    @Override
    public ItemDto getItemInfo(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        Booking lastBooking;
        Booking nextBooking;
        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsBefore(
                    itemId, APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.DESC, "end")).orElse(null);
            nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartIsAfter(
                    itemId, APPROVED, LocalDateTime.now(),
                    Sort.by(Sort.Direction.ASC, "start")).orElse(null);
        } else {
            lastBooking = null;
            nextBooking = null;
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        log.info("Success! Here's your itemInfo.");
        return ItemMapper.INSTANCE.toItemDtoOwner(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemDto> getOwnerItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId, Sort.by(Sort.DEFAULT_DIRECTION, "id"));
        log.info("Find owner items with id: {}", userId);
        List<Long> ids = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItem_IdIn(ids);
        log.info("Find bookings.");
        List<Booking> lastBookings = bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(now))
                .collect(Collectors.toList());
        List<Booking> nextBookings = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItem_IdIn(ids);
        log.info("Find comments.");
        return items.stream().map(item -> {
            List<Comment> c = comments.stream()
                    .filter(comment -> comment.getItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());
            Booking lastBooking = lastBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);
            Booking nextBooking = nextBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
            return ItemMapper.INSTANCE.toItemDtoOwner(item, lastBooking, nextBooking, c);
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsByKeyword(String text) {
        if (text.isBlank()) {
            log.info("Blank for blank, that's fair.");
            return List.of();
        } else {
            log.info("Items by text: {}", text);
            return ItemMapper.INSTANCE.toItemDtoList(itemRepository.search(text));
        }
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Item itemFromRepo = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found."));
        if (!itemFromRepo.getOwner().equals(user)) {
            throw new ForbiddenException(
                    "Error! You don't have permission to access this option." +
                            " Only the owner of the item can update it.");
        }
        itemDto.setId(itemId);
        itemDto.setName(itemDto.getName() == null ?
                itemFromRepo.getName() :
                itemDto.getName()
        );
        itemDto.setDescription(itemDto.getDescription() == null ?
                itemFromRepo.getDescription() :
                itemDto.getDescription()
        );
        itemDto.setAvailable(itemDto.getAvailable() == null ?
                itemFromRepo.getAvailable() :
                itemDto.getAvailable()
        );
        if (isValid(itemDto)) {
            Item item = itemRepository.save(ItemMapper.INSTANCE.toItemWithId(itemDto, user));
            log.info("Success! Updated item: {}", item);
            return ItemMapper.INSTANCE.toItemDto(item);
        } else {
            throw new BadRequestException("Invalid data to update.");
        }
    }

    private boolean isValid(ItemDto itemDto) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        return violations.isEmpty();
    }
}