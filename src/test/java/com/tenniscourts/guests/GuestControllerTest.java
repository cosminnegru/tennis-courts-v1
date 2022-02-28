package com.tenniscourts.guests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GuestControllerTest extends BaseControllerTest {

    public static final String ROGER_FEDERER = "Roger Federer";
    public static final String RAFAEL_NADAL = "Rafael Nadal";

    @Test
    public void should_return_all_guests() throws Exception {
        MvcResult result = mockMvc.perform(get("/guests"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        List<GuestDTO> guests = objectMapper.readValue(json, List.class);

        assertThat(guests.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void should_return_guest_by_id() throws Exception {
        MvcResult result = mockMvc.perform(get("/guests/1"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        GuestDTO guest = objectMapper.readValue(json, GuestDTO.class);

        assertThat(guest.getName()).isEqualTo(ROGER_FEDERER);
    }

    @Test
    public void should_return_404_if_guest_not_found() throws Exception {
        mockMvc.perform(get("/guests/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_return_guest_by_name() throws Exception {
        MvcResult result = mockMvc.perform(get("/guests/guest").param("name", ROGER_FEDERER))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        GuestDTO guest = objectMapper.readValue(json, GuestDTO.class);

        assertThat(guest.getName()).isEqualTo(ROGER_FEDERER);
    }

    @Test
    public void should_add_new_guest() throws Exception {
        GuestDTO guestRequestDTO = new GuestDTO();
        guestRequestDTO.setName("Dummy Name");

        MvcResult result = mockMvc.perform(post("/guests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        GuestDTO actualGuestDTO = new ObjectMapper().readValue(json, GuestDTO.class);

        assertThat(actualGuestDTO.getName()).isEqualTo(guestRequestDTO.getName());
    }

    @Test
    public void should_return_400_if_add_new_guest_without_name() throws Exception {
        mockMvc.perform(post("/guests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new GuestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_update_guest() throws Exception {
        GuestDTO guestRequestDTO = new GuestDTO();
        guestRequestDTO.setId(2L);
        guestRequestDTO.setName("Dummy Name");

        MvcResult result = mockMvc.perform(put("/guests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        GuestDTO actualGuestDTO = new ObjectMapper().readValue(json, GuestDTO.class);

        assertThat(actualGuestDTO.getName()).isEqualTo(guestRequestDTO.getName());
    }

    @Test
    public void should_return_404_if_guest_to_be_updated_not_found() throws Exception {
        GuestDTO guestRequestDTO = new GuestDTO();
        guestRequestDTO.setId(100L);
        guestRequestDTO.setName("Dummy Name");

        mockMvc.perform(put("/guests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(guestRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_delete_guest() throws Exception {
        mockMvc.perform(delete("/guests/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/guests/2"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void should_return_404_if_guest_to_be_deleted_is_not_found() throws Exception {
        mockMvc.perform(delete("/guests/100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
