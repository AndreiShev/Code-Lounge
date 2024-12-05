package ru.skillbox.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.skillbox.dto.UnreadCountDto;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class DialogControllerTest extends AbstractTest {


    @Test
    @WithMockUser(username = AUTHOR_UUID)
    void testGetDialog_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/dialogs/recipientId/" + RECIPIENT_UUID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.conversationPartner1").value(RECIPIENT_UUID))
                .andExpect(jsonPath("$.lastMessage").isArray());
    }

    @Test
    @WithMockUser(username = AUTHOR_UUID)
    void TestSetStatusMessageRead_shouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/v1/dialogs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = AUTHOR_UUID)
    void getAllDialogs_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/dialogs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = AUTHOR_UUID)
    void TestGetUnreadMessageCount_shouldReturnOk() throws Exception {
        UnreadCountDto rs = UnreadCountDto.builder()
                        .count(1L).build();

        mockMvc.perform(get("/api/v1/dialogs/unread"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(rs));
    }

    @Test
    @WithMockUser(username = AUTHOR_UUID)
    void getAllMessages_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/dialogs/messages?recipientId=" + RECIPIENT_UUID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.content").isArray());
    }
}