package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestMapper;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleMapper;
import com.tenniscourts.schedules.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final GuestService guestService;
    private final GuestMapper guestMapper;
    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    @Transactional
    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Guest guest = guestMapper.map(guestService.findById(createReservationRequestDTO.getGuestId()));
        Schedule schedule = scheduleMapper.map(scheduleService.findSchedule(createReservationRequestDTO.getScheduleId()));

        checkScheduleIsBeforeCurrentTime(schedule);
        checkReservationAlreadyExists(schedule);

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .schedule(schedule)
                .reservationStatus(ReservationStatus.READY_TO_PLAY)
                .value(BigDecimal.TEN)
                .build();

        return reservationMapper.map(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        checkScheduleIsBeforeCurrentTime(reservation.getSchedule());
    }

    private void checkScheduleIsBeforeCurrentTime(Schedule schedule) {
        if (schedule.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule/book only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        return BigDecimal.ZERO;
    }

    @Transactional
    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
        Reservation previousReservation = reservationMapper.map(findReservation(previousReservationId));

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        reschedule(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));

        return newReservation;
    }

    private void reschedule(Reservation previousReservation) {
        validateCancellation(previousReservation);
        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);
    }

    private void checkReservationAlreadyExists(Schedule schedule) {
        List<Reservation> reservations = reservationRepository.findBySchedule_Id(schedule.getId());
        boolean reservationAlreadyExists = reservations.stream()
                .anyMatch(reservation -> ReservationStatus.READY_TO_PLAY == reservation.getReservationStatus());
        if (reservationAlreadyExists) {
            throw new AlreadyExistsEntityException("Reservation already exists for schedule " + schedule.getStartDateTime() + " and tennis court " + schedule.getTennisCourt().getName());
        }
    }
}
