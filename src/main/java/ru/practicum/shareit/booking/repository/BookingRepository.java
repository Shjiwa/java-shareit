package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByEndDesc(Long bookerId);

    List<Booking> findByBooker_IdAndStatusOrderByEndDesc(Long bookerId, Status status);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime time, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsBefore(Long itemId, Status status,
                                                                 LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsAfter(Long itemId, Status status,
                                                                LocalDateTime time, Sort sort);

    @Query("select b " +
           "from Booking b " +
           "join Item i on (i.id = b.item.id) " +
           "join User u on (u.id = b.booker.id) " +
           "where b.booker.id = ?1 and i.id = ?2 and b.end < ?3")
    List<Booking> findBookingsByBooker_IdAAndItem_IdInPast(Long bookerId, Long itemId, LocalDateTime time);

    @Query("select b " +
           "from Booking b " +
           "join Item i on (i.id = b.item.id) " +
           "join User u on (u.id = i.owner.id) " +
           "where i.owner.id = ?1")
    List<Booking> findAllBookings(Long ownerId, Sort sort);

    @Query("select b " +
           "from Booking b " +
           "join Item i on (i.id = b.item.id) " +
           "join User u on (u.id = i.owner.id) " +
           "where i.owner.id = ?1 " +
           "and b.start <= ?2 " +
           "and b.end >= ?2")
    List<Booking> findCurrentBookings(Long ownerId, LocalDateTime time, Sort sort);

    @Query("select b " +
           "from Booking b " +
           "join Item i on (i.id = b.item.id) " +
           "join User u on (u.id = i.owner.id) " +
           "where i.owner.id = ?1 " +
           "and b.start < ?2 " +
           "and b.end < ?2")
    List<Booking> findPastBookings(Long ownerId, LocalDateTime time, Sort sort);

    @Query("select b " +
           "from Booking b " +
           "join Item i on (i.id = b.item.id) " +
           "join User u on (u.id = i.owner.id) " +
           "where i.owner.id = ?1 " +
           "and b.start > ?2 " +
           "and b.end > ?2")
    List<Booking> findFutureBookings(Long ownerId, LocalDateTime time, Sort sort);

    @Query("select b " +
           "from Booking b " +
           "join Item i on (i.id = b.item.id) " +
           "join User u on (u.id = i.owner.id) " +
           "where i.owner.id = ?1 " +
           "and b.status = ?2")
    List<Booking> findStatusBookings(Long ownerId, Status status, Sort sort);
}
