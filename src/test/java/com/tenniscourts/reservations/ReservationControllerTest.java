package com.tenniscourts.reservations;

import com.tenniscourts.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationControllerTest extends BaseControllerTest {

    @Test
    public void should_book_new_reservation() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(1L)
                .scheduleId(5L)
                .build();

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_return_404_when_guest_is_not_found() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(100L)
                .scheduleId(1L)
                .build();

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_404_when_schedule_is_not_found() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(1L)
                .scheduleId(100L)
                .build();

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_400_when_schedule_is_in_the_past() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(1L)
                .scheduleId(6L)
                .build();

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_return_409_if_reservation_already_exists() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder()
                .guestId(1L)
                .scheduleId(2L)
                .build();

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReservationRequestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_cancel_reservation() throws Exception {
        mockMvc.perform(put("/reservations/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_return_404_when_cancel_reservation_does_not_exists() throws Exception {
        mockMvc.perform(put("/reservations/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_400_when_cancel_reservation_in_wrong_status() throws Exception {
        mockMvc.perform(put("/reservations/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_reschedule_reservation() throws Exception {
        mockMvc.perform(put("/reservations/5/schedules/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
