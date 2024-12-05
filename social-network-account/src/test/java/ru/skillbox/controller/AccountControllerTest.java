package ru.skillbox.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import ru.skillbox.AbstractTest;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.entity.Account;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

class AccountControllerTest extends AbstractTest {

    @Test
    @DisplayName("GetAccount, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testGetAccount_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(UUID_200_1))
                .andExpect(jsonPath("$.email").value("test1@example.com"));
    }

    @Test
    @DisplayName("GetAccount, should return 404")
    @WithMockUser(username = UUID_404)
    void testGetAccount_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GetAccount, should return 401")
    void testGetAccount_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("DeleteAccount, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testDeleteAccount_shouldReturnOk() throws Exception {

        Account accountBeforeDeletion = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertFalse(accountBeforeDeletion.getIsDeleted());

        mockMvc.perform(delete("/api/v1/account/me"))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESSFULLY));

        entityManager.clear();

        Account accountAfterDeletion = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertEquals(accountBeforeDeletion.getId(), accountAfterDeletion.getId());
        assertNotNull(accountAfterDeletion);
        assertTrue(accountAfterDeletion.getIsDeleted());
        assertFalse(accountAfterDeletion.getIsOnline());

    }

    @Test
    @DisplayName("DeleteAccount, should return 404")
    @WithMockUser(username = UUID_404)
    void testDeleteAccount_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/account/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DeleteAccount, should return 401")
    void testDeleteAccount_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/v1/account/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GetAllAccounts, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testGetAllAccounts_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/account")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(UUID_200_1))
                .andExpect(jsonPath("$.content[0].email").value("test1@example.com"))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GetAllAccounts, should return 401")
    void testGetAllAccounts_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("UpdateAccount, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testUpdateAccount_shouldReturnOk() throws Exception {

        Account accountBeforeUpdate = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertFalse(accountBeforeUpdate.getIsDeleted());
        assertEquals("Россия", accountBeforeUpdate.getCountry());
        assertEquals("Москва", accountBeforeUpdate.getCity());
        assertEquals("Name1", accountBeforeUpdate.getFirstName());
        assertEquals("Surname1", accountBeforeUpdate.getLastName());
        assertNull(accountBeforeUpdate.getPhone());
        assertNull(accountBeforeUpdate.getAbout());
        assertNull(accountBeforeUpdate.getProfileCover());
        assertNull(accountBeforeUpdate.getEmojiStatus());
        assertNull(accountBeforeUpdate.getPhoto());

        AccountDto accountDto = AccountDto.builder()
                .id(accountBeforeUpdate.getId())
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .city("UpdatedCity")
                .country("UpdatedCountry")
                .about("UpdatedAbout")
                .profileCover("UpdatedProfileCover")
                .emojiStatus("1")
                .photo("UpdatedPhoto")
                .phone("78887776655")
                .build();

        mockMvc.perform(put("/api/v1/account/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk());

        Account accountAfterUpdate = accountRepository.findById(accountBeforeUpdate.getId()).get();

        assertEquals(accountBeforeUpdate.getId(), accountAfterUpdate.getId());
        assertEquals(accountBeforeUpdate.getEmail(), accountAfterUpdate.getEmail());
        assertEquals("UPDATEDFIRSTNAME", accountAfterUpdate.getFirstName());
        assertEquals("UPDATEDLASTNAME", accountAfterUpdate.getLastName());
        assertEquals("UpdatedCity", accountAfterUpdate.getCity());
        assertEquals("UpdatedCountry", accountAfterUpdate.getCountry());
        assertEquals("UpdatedAbout", accountAfterUpdate.getAbout());
        assertEquals("UpdatedProfileCover", accountAfterUpdate.getProfileCover());
        assertEquals("1", accountAfterUpdate.getEmojiStatus());
        assertEquals("UpdatedPhoto", accountAfterUpdate.getPhoto());
        assertEquals("78887776655", accountAfterUpdate.getPhone());

    }

    @Test
    @DisplayName("UpdateAccount, should return 401")
    void testUpdateAccount_shouldReturnUnauthorized() throws Exception {

        AccountDto accountDto = AccountDto.builder()
                .id(UUID.fromString(UUID_404))
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .city("UpdatedCity")
                .country("UpdatedCountry")
                .about("UpdatedAbout")
                .profileCover("UpdatedProfileCover")
                .emojiStatus("1")
                .photo("UpdatedPhoto")
                .build();

        mockMvc.perform(put("/api/v1/account/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("UpdateAccount, correct phone, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testUpdatePhone_correctPhone_shouldReturnOk() throws Exception {

        Account accountBeforeUpdate = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertNull(accountBeforeUpdate.getPhone());

        AccountDto accountDto = AccountDto.builder()
                .id(accountBeforeUpdate.getId())
                .phone("71111111111")
                .build();

        mockMvc.perform(put("/api/v1/account/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.phone").value("71111111111"));
    }

    @Test
    @DisplayName("UpdateAccount, correct phone without 7, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testUpdatePhone_correctPhoneWithout7_shouldReturnOk() throws Exception {

        Account accountBeforeUpdate = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertNull(accountBeforeUpdate.getPhone());

        AccountDto accountDto = AccountDto.builder()
                .id(accountBeforeUpdate.getId())
                .phone("1111111111")
                .build();

        mockMvc.perform(put("/api/v1/account/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.phone").value("71111111111"));
    }

    @Test
    @DisplayName("UpdateAccount, incorrect phone, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testUpdatePhone_incorrectPhone_shouldReturnOk() throws Exception {

        Account accountBeforeUpdate = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertNull(accountBeforeUpdate.getPhone());

        AccountDto accountDto = AccountDto.builder()
                .id(accountBeforeUpdate.getId())
                .phone("711111111")
                .build();

        mockMvc.perform(put("/api/v1/account/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isOk());

        Account accountAfterUpdate = accountRepository.findById(UUID.fromString(UUID_200_1)).get();

        assertNull(accountAfterUpdate.getPhone());

    }

    @Test
    @DisplayName("GetAccountById, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testGetAccountById_shouldReturnOk() throws Exception {

        UUID accountId = UUID.fromString(UUID_200_1);

        mockMvc.perform(get("/api/v1/account/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.email").value("test1@example.com"));
    }

    @Test
    @DisplayName("GetAccountById, should return 401")
    void testGetAccountById_shouldReturnUnauthorized() throws Exception {

        UUID accountId = UUID.fromString(UUID_200_1);

        mockMvc.perform(get("/api/v1/account/{id}", accountId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GetAccountById, should return 404")
    @WithMockUser(username = UUID_200_1)
    void testGetAccountById_shouldReturnNotFound() throws Exception {

        UUID accountId = UUID.fromString(UUID_404);

        mockMvc.perform(get("/api/v1/account/{id}", accountId))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("BlockAndUnblockAccount, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testBlockAndUnblockAccount_shouldReturnOk() throws Exception {
        UUID accountId = UUID.fromString(UUID_200_1);

        Account accountBeforeBlock = accountRepository.findById(accountId).get();

        assertFalse(accountBeforeBlock.getIsBlocked());

        mockMvc.perform(put("/api/v1/account/block/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESSFULLY));

        entityManager.clear();

        Account accountAfterBlock = accountRepository.findById(accountId).get();

        assertEquals(accountBeforeBlock.getId(), accountAfterBlock.getId());
        assertNotNull(accountAfterBlock.getIsBlocked());
        assertTrue(accountAfterBlock.getIsBlocked());

        mockMvc.perform(delete("/api/v1/account/block/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string(SUCCESSFULLY));

        entityManager.clear();

        Account accountAfterUnblock = accountRepository.findById(accountId).get();

        assertEquals(accountBeforeBlock.getId(), accountAfterUnblock.getId());
        assertNotNull(accountAfterUnblock.getIsBlocked());
        assertFalse(accountAfterUnblock.getIsBlocked());
    }

    @Test
    @DisplayName("BlockAccount, should return 404")
    @WithMockUser(username = UUID_200_1)
    void testBlockAccount_shouldReturnNotFound() throws Exception {

        UUID accountId = UUID.fromString(UUID_404);

        mockMvc.perform(put("/api/v1/account/block/{id}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("UnblockAccount, should return 404")
    @WithMockUser(username = UUID_200_1)
    void testUnblockAccount_shouldReturnNotFound() throws Exception {

        UUID accountId = UUID.fromString(UUID_404);

        mockMvc.perform(delete("/api/v1/account/block/{id}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("BlockAccount, should return 401")
    void testBlockAccount_shouldReturnUnauthorized() throws Exception {

        UUID accountId = UUID.fromString(UUID_200_1);

        Account accountBeforeBlock = accountRepository.findById(accountId).get();

        assertFalse(accountBeforeBlock.getIsBlocked());

        mockMvc.perform(put("/api/v1/account/block/{id}", accountId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("UnblockAccount, should return 401")
    void testUnblockAccount_shouldReturnUnauthorized() throws Exception {

        UUID accountId = UUID.fromString(UUID_200_1);

        mockMvc.perform(delete("/api/v1/account/block/{id}", accountId))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("SearchAccount, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testSearchAccount_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/api/v1/account/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("country", "Россия")
                        .param("city", "Москва")
                        .param("isBlocked", String.valueOf(false))
                        .param("isDeleted", String.valueOf(false))
                        .param("ageFrom", "20")
                        .param("ageTo", "35")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("SearchAccount, by Author - firstName, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testSearchAccount_byAuthor_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/api/v1/account/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstName", "Test1 User1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("SearchAccount, by firstName - author, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testSearchAccount_byFirstName_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/api/v1/account/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("author", "Test1")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("SearchAccount, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testSearchAccount_byIds_shouldReturnOk() throws Exception {

        List<UUID> ids = Arrays.asList(UUID.fromString(UUID_200_2), UUID.fromString(UUID_200_3));

        mockMvc.perform(get("/api/v1/account/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.get(0).toString())
                        .param("ids", ids.get(1).toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GetAllAccountIds, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testGetAllAccountIds_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/api/v1/account/ids")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(hasSize(5)));
    }

    @Test
    @DisplayName("GetAllAccountIds, should return 401")
    void testGetAllAccountIds_shouldReturnUnauthorized() throws Exception {

        mockMvc.perform(get("/api/v1/account/ids")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GetAccountsByTheirIds, should return 200")
    @WithMockUser(username = UUID_200_1)
    void testGetAccountsByTheirIds_shouldReturnOk() throws Exception {

        List<UUID> ids = Arrays.asList(UUID.fromString(UUID_200_2), UUID.fromString(UUID_200_3));

        mockMvc.perform(get("/api/v1/account/accountIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.get(0).toString())
                        .param("ids", ids.get(1).toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GetAccountsByTheirIds, should return 401")
    void testGetAccountsByTheirIds_shouldReturnUnauthorized() throws Exception {

        List<UUID> ids = Arrays.asList(UUID.fromString(UUID_200_2), UUID.fromString(UUID_200_3));

        mockMvc.perform(get("/api/v1/account/accountIds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("ids", ids.get(0).toString())
                        .param("ids", ids.get(1).toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "firstName,asc"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("SearchAccountsByAuthor, should return 200 with correct author")
    @WithMockUser(username = UUID_200_1)
    void testSearchAccountsByAuthor_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/account/searchs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("author", "Name1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("1" + UUID_200))
                .andExpect(jsonPath("$[0].firstName").value("Name1"))
                .andExpect(jsonPath("$[0].lastName").value("Surname1"))
                .andExpect(jsonPath("$[0].email").value("test1@example.com"));
    }

    @Test
    @DisplayName("SearchAccountsByAuthor, should return 200 with no results for non-existing author")
    @WithMockUser(username = UUID_200_1)
    void testSearchAccountsByAuthor_shouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/account/searchs")
                        .param("author", "NonExistingName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("SearchAccountsByAuthor, should return 401 when unauthorized")
    void testSearchAccountsByAuthor_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/account/searchs")
                        .param("author", "Name1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpServletResponse.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists());
    }

}
