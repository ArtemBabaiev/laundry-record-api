package com.undefined.laundry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undefined.laundry.model.request.AddEntryRequest;
import com.undefined.laundry.model.request.DeleteEntryRequest;
import com.undefined.laundry.model.request.UpdateEntryRequest;
import com.undefined.laundry.model.response.AccountEntryResponse;
import com.undefined.laundry.model.response.LaundryEntryResponse;
import com.undefined.laundry.model.response.WriteEntryResponse;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/mock_data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class LaundryQueueControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;
	String baseUrl = "/laundry-queue";

	@Test
	void testFetchQueue_Ok() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl).param("date", LocalDate.now().toString()).param("floor",
				String.valueOf(1));
		MvcResult result = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();

		Map<LocalTime, LaundryEntryResponse> body = mapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<Map<LocalTime, LaundryEntryResponse>>() {
				});
		assertNotNull(body);
		assertTrue(!body.isEmpty());
	}

	@Test
	void testFetchQueue_BadRequest_NoDateParam() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl).param("floor", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchQueue_BadRequest_NoFloorParam() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl).param("date", LocalDate.now().toString());
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchQueue_BadRequest_PastDate() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl).param("date", (LocalDate.now().minusDays(1)).toString())
				.param("floor", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchAvailableTime_Ok() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/available").param("date", LocalDate.now().toString())
				.param("floor", String.valueOf(1));
		MvcResult result = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();

		List<String> body = mapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<List<String>>() {
				});
		assertNotNull(body);
		assertTrue(!body.isEmpty());
	}

	@Test
	void testFetchAvailableTime_BadRequest_NoDateParam() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/available").param("floor", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchAvailableTime_BadRequest_NoFloorParam() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/available").param("date", LocalDate.now().toString());
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchAvailableTime_BadRequest_PastDate() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/available")
				.param("date", (LocalDate.now().minusDays(1)).toString()).param("floor", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchEntriesForAccount_Ok() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/account").param("telegramId", String.valueOf(1))
				.param("floor", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isOk());

		MvcResult result = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();

		List<AccountEntryResponse> body = mapper.readValue(result.getResponse().getContentAsString(),
				new TypeReference<List<AccountEntryResponse>>() {
				});
		assertNotNull(body);
		assertTrue(!body.isEmpty());
	}

	@Test
	void testFetchEntriesForAccount_BadRequest_NoTelegramId() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/account").param("floor", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testFetchEntriesForAccount_BadRequest_NoFloor() throws Exception {
		MockHttpServletRequestBuilder builder = get(baseUrl + "/account").param("telegramId", String.valueOf(1));
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	private AddEntryRequest getAddEntryRequest() {
		AddEntryRequest request = new AddEntryRequest();
		request.setDate(LocalDate.now().plusDays(1L));
		request.setFloor(1);
		request.setFullName("Artem");
		request.setRoom("103");
		request.setTelegramId(1L);
		request.setTime(LocalTime.of(7, 0));
		request.setUsername("artem");
		return request;
	}

	@Test
	void testPostLaundryEntry_Ok() throws Exception {
		AddEntryRequest request = getAddEntryRequest();

		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);

		MvcResult result = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();

		WriteEntryResponse body = mapper.readValue(result.getResponse().getContentAsString(), WriteEntryResponse.class);
		assertNotNull(body);
		assertNotNull(body.getUuid());
	}

	@Test
	void testPostLaundryEntry_BadRequest_MalformedBody() throws Exception {
		AddEntryRequest request = getAddEntryRequest();
		request.setTelegramId(null);
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testPostLaundryEntry_BadRequest_ProvidedDateTimeIsOccupied() throws Exception {
		AddEntryRequest request = getAddEntryRequest();
		request.setTime(LocalTime.of(16, 0));
		request.setDate(LocalDate.now());
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testPostLaundryEntry_BadRequest_PastDateTimeInBody() throws Exception {
		AddEntryRequest request = getAddEntryRequest();
		request.setDate(LocalDate.now().minusDays(1L));
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = post(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	private UpdateEntryRequest getUpdateEntryRequest() {
		UpdateEntryRequest request = new UpdateEntryRequest();
		request.setUuid(UUID.fromString("c002dfcd-ff2e-4311-b718-2ad8e313b742"));
		request.setTelegramId(1L);
		request.setDate(LocalDate.now().plusDays(1L));
		request.setFloor(1);
		request.setTime(LocalTime.of(8, 0));
		return request;
	}

	@Test
	void testPutLaundryEntry_Ok() throws Exception {
		UpdateEntryRequest request = getUpdateEntryRequest();
		var updatedTime = LocalTime.of(23, 0);
		request.setTime(updatedTime);
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);

		MvcResult result = mockMvc.perform(builder).andExpect(status().isOk()).andReturn();

		WriteEntryResponse body = mapper.readValue(result.getResponse().getContentAsString(), WriteEntryResponse.class);
		assertNotNull(body);
		assertEquals(request.getUuid(), body.getUuid());
		assertEquals(updatedTime, body.getTime());
	}

	@Test
	void testPutLaundryEntry_BadRequest_MalformedBody() throws Exception {
		UpdateEntryRequest request = getUpdateEntryRequest();
		request.setTelegramId(null);
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testPutLaundryEntry_BadRequest_PastDateTimeInBody() throws Exception {
		UpdateEntryRequest request = getUpdateEntryRequest();
		request.setDate(LocalDate.now().minusDays(1L));
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testPutLaundryEntry_BadRequest_ProvidedDateTimeIsOccupied() throws Exception {
		UpdateEntryRequest request = getUpdateEntryRequest();
		request.setTime(LocalTime.of(16, 0));
		request.setDate(LocalDate.now());
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testPutLaundryEntry_NotFound_NonExistentUuid() throws Exception {
		UpdateEntryRequest request = getUpdateEntryRequest();
		request.setUuid(UUID.randomUUID());
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isNotFound());
	}

	@Test
	void testPutLaundryEntry_Unauthorized_ProvidedTelegramIdIsNotOwner() throws Exception {
		UpdateEntryRequest request = getUpdateEntryRequest();
		request.setTelegramId(3L);
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = put(baseUrl).contentType(MediaType.APPLICATION_JSON).content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isUnauthorized());
	}

	private DeleteEntryRequest getDeleteEntryRequest() {
		DeleteEntryRequest request = new DeleteEntryRequest();
		request.setUuid(UUID.fromString("0bad90d0-a0e2-4876-a3d0-1dcc98ac3ea6"));
		request.setTelegramId(1L);
		return request;
	}

	@Test
	void testDeleteLaundryEntry_Ok() throws Exception {
		DeleteEntryRequest request = getDeleteEntryRequest();

		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = delete(baseUrl).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isNoContent());
	}

	@Test
	void testDeleteLaundryEntry_BadRequest_MalformedBody() throws Exception {
		DeleteEntryRequest request = getDeleteEntryRequest();
		request.setTelegramId(null);
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = delete(baseUrl).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isBadRequest());
	}

	@Test
	void testDeleteLaundryEntry_NotFound_NonExistentUuid() throws Exception {
		DeleteEntryRequest request = getDeleteEntryRequest();
		request.setUuid(UUID.randomUUID());
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = delete(baseUrl).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isNotFound());
	}

	@Test
	void testDeleteLaundryEntry_Unauthorized_ProvidedTelegramIdIsNotOwner() throws Exception {
		DeleteEntryRequest request = getDeleteEntryRequest();
		request.setTelegramId(3L);
		String jsonBody = mapper.writeValueAsString(request);

		MockHttpServletRequestBuilder builder = delete(baseUrl).contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody);
		mockMvc.perform(builder).andExpect(status().isUnauthorized());
	}
}
