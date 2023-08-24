package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constant.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByEndDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end,
                                                                             Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByEndDesc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findAllByItem_OwnerId(Long ownerId, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long ownerId, LocalDateTime start,
                                                                                   LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(Long ownerId, LocalDateTime start,
                                                                                    LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStartIsAfterAndEndIsAfterOrderByEndDesc(Long ownerId, LocalDateTime start,
                                                                                  LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_OwnerIdAndStatus(Long ownerId, Status status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsBefore(Long itemId, Status status,
                                                                 LocalDateTime time, Sort sort);

    Optional<Booking> findFirstByItemIdAndStatusAndStartIsAfter(Long itemId, Status status,
                                                                LocalDateTime time, Sort sort);

    List<Booking> findAllByItem_IdIn(List<Long> ids);

    List<Booking> findAllByBooker_IdAndItem_IdAndStartIsBeforeAndEndIsBefore(Long bookerId, Long itemId,
                                                                             LocalDateTime time, LocalDateTime time2);
}