package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(uses = {CommentMapper.class})
public interface ItemMapper {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", source = "owner")
    Item toItem(ItemDto itemDto, User owner);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", source = "owner")
    Item toItemWithId(ItemDto itemDto, User owner);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "lastBooking", source = "lastBooking", qualifiedByName = "BookingToBookingDtoOwner")
    @Mapping(target = "nextBooking", source = "nextBooking", qualifiedByName = "BookingToBookingDtoOwner")
    @Mapping(target = "comments", source = "comments")
    ItemDtoOwner toItemDtoOwner(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments);

    List<ItemDto> toItemDtoList(List<Item> items);

    @Named("BookingToBookingDtoOwner")
    static BookingDtoOwner toBookingDtoOwner(Booking booking) {
        return BookingMapper.INSTANCE.toBookingDtoOwner(booking);
    }
}