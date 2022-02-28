package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final TennisCourtRepository tennisCourtRepository;
    private final TennisCourtMapper tennisCourtMapper;

    @Transactional
    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {

        checkInputParameters(createScheduleRequestDTO);
        checkScheduleAlreadyExists(tennisCourtId, createScheduleRequestDTO);

        Schedule schedule = createNewSchedule(tennisCourtId, createScheduleRequestDTO);
        return scheduleMapper.map(scheduleRepository.save(schedule));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        //TODO: implement
        return null;
    }

    @Transactional(readOnly = true)
    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Schedule with id " + scheduleId + " not found.");
        });
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    private void checkInputParameters(CreateScheduleRequestDTO createScheduleRequestDTO) {
        if (createScheduleRequestDTO.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot add schedules in the past.");
        }
    }

    private void checkScheduleAlreadyExists(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        if (scheduleRepository.findByTennisCourt_IdAndStartDateTime(
                tennisCourtId, createScheduleRequestDTO.getStartDateTime()).isPresent()) {
            throw new AlreadyExistsEntityException("There is already a schedule slot created for the given start date and time.");
        }
    }

    private Schedule createNewSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        TennisCourtDTO tennisCourtDTO = getTennisCourtDTO(tennisCourtId);

        LocalDateTime scheduleStart = createScheduleRequestDTO.getStartDateTime();
        LocalDateTime scheduleEnd = scheduleStart.plusHours(1L);

        return Schedule.builder()
                .tennisCourt(tennisCourtMapper.map(tennisCourtDTO))
                .startDateTime(scheduleStart)
                .endDateTime(scheduleEnd)
                .build();
    }

    private TennisCourtDTO getTennisCourtDTO(Long tennisCourtId) {
        return tennisCourtRepository.findById(tennisCourtId).map(tennisCourtMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Tennis Court not found.");
        });
    }

}
