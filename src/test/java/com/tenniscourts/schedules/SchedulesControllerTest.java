package com.tenniscourts.schedules;

import com.tenniscourts.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SchedulesControllerTest extends BaseControllerTest {

    @Test
    public void should_add_new_schedule_for_tennis_court() throws Exception {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.now().plusYears(1L));
        createScheduleRequestDTO.setTennisCourtId(1L);

        mockMvc.perform(post("/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createScheduleRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_return_400_if_schedule_in_the_past() throws Exception {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.now().minusYears(1L));
        createScheduleRequestDTO.setTennisCourtId(1L);

        mockMvc.perform(post("/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createScheduleRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_409_if_another_schedule_exists_at_the_same_time() throws Exception {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.parse("2023-12-20T20:00:00.0"));
        createScheduleRequestDTO.setTennisCourtId(1L);

        mockMvc.perform(post("/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createScheduleRequestDTO)))
                .andExpect(status().isConflict());
    }
}
