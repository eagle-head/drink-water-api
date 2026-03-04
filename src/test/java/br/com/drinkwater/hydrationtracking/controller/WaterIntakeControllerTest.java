package br.com.drinkwater.hydrationtracking.controller;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.drinkwater.api.v1.controller.WaterIntakeControllerV1;
import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.core.CursorPageResponse;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = WaterIntakeControllerV1.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
final class WaterIntakeControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private WaterIntakeService waterIntakeService;

    @Test
    void
            givenValidJwtTokenAndValidWaterIntakeDTO_whenCreateWaterIntake_thenReturnCreatedWaterIntakeResponse()
                    throws Exception {
        // Given
        when(waterIntakeService.create(WATER_INTAKE_DTO, USER_UUID))
                .thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When & Then
        var result =
                mockMvc.perform(
                                post("/api/v1/users/water-intakes")
                                        .with(
                                                jwt().jwt(
                                                                builder ->
                                                                        builder.claim(
                                                                                "sub",
                                                                                USER_UUID
                                                                                        .toString()))
                                                        .authorities(
                                                                new SimpleGrantedAuthority(
                                                                        "SCOPE_drinkwater:v1:waterintake:entry:create")))
                                        .content(objectMapper.writeValueAsString(WATER_INTAKE_DTO))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .header("Accept-Language", "en-US"))
                        .andExpect(status().isCreated());

        expectWaterIntakeResponse(result);
    }

    @Test
    void givenValidJwtTokenAndExistingId_whenFindWaterIntakeById_thenReturnWaterIntakeResponse()
            throws Exception {
        // Given
        when(waterIntakeService.findByIdAndUserId(WATER_INTAKE_ID, USER_UUID))
                .thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When & Then
        var result =
                mockMvc.perform(
                                get("/api/v1/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                                        .with(
                                                jwt().jwt(
                                                                builder ->
                                                                        builder.claim(
                                                                                "sub",
                                                                                USER_UUID
                                                                                        .toString()))
                                                        .authorities(
                                                                new SimpleGrantedAuthority(
                                                                        "SCOPE_drinkwater:v1:waterintake:entry:read")))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .header("Accept-Language", "en-US"))
                        .andExpect(status().isOk());

        expectWaterIntakeResponse(result);
    }

    @Test
    void
            givenValidJwtTokenAndValidWaterIntakeDTO_whenUpdateWaterIntakeById_thenReturnUpdatedWaterIntakeResponse()
                    throws Exception {
        // Given
        when(waterIntakeService.update(WATER_INTAKE_ID, WATER_INTAKE_DTO, USER_UUID))
                .thenReturn(RESPONSE_WATER_INTAKE_DTO);

        // When & Then
        var result =
                mockMvc.perform(
                                put("/api/v1/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                                        .with(
                                                jwt().jwt(
                                                                builder ->
                                                                        builder.claim(
                                                                                "sub",
                                                                                USER_UUID
                                                                                        .toString()))
                                                        .authorities(
                                                                new SimpleGrantedAuthority(
                                                                        "SCOPE_drinkwater:v1:waterintake:entry:update")))
                                        .content(objectMapper.writeValueAsString(WATER_INTAKE_DTO))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .header("Accept-Language", "en-US"))
                        .andExpect(status().isOk());

        expectWaterIntakeResponse(result);
    }

    @Test
    void givenValidJwtTokenAndExistingId_whenDeleteWaterIntakeById_thenReturnNoContent()
            throws Exception {
        // Given
        doNothing().when(waterIntakeService).deleteByIdAndUserId(WATER_INTAKE_ID, USER_UUID);

        // When & Then
        mockMvc.perform(
                        delete("/api/v1/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:waterintake:entry:delete")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isNoContent());

        verify(waterIntakeService, times(1)).deleteByIdAndUserId(WATER_INTAKE_ID, USER_UUID);
    }

    @Test
    void givenValidJwtTokenAndValidFilter_whenSearchWaterIntakes_thenReturnCursorPageResponse()
            throws Exception {
        // Given
        Instant startDate = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS);
        Instant endDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        var filterDTO =
                new WaterIntakeFilterDTO(
                        startDate, endDate, 100, 2000, null, 10, "dateTimeUTC", "DESC");

        var cursorPageResponse =
                new CursorPageResponse<>(List.of(RESPONSE_WATER_INTAKE_DTO), 10, false, null);

        when(waterIntakeService.search(filterDTO, USER_UUID)).thenReturn(cursorPageResponse);

        // When & Then
        mockMvc.perform(
                        get("/api/v1/users/water-intakes")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:waterintake:entries:search")))
                                .param("startDate", startDate.toString())
                                .param("endDate", endDate.toString())
                                .param("minVolume", "100")
                                .param("maxVolume", "2000")
                                .param("size", "10")
                                .param("sortField", "dateTimeUTC")
                                .param("sortDirection", "DESC")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(RESPONSE_WATER_INTAKE_DTO.id()))
                .andExpect(jsonPath("$.content[0].dateTimeUTC").value(DATE_TIME_UTC.toString()))
                .andExpect(
                        jsonPath("$.content[0].volume").value(RESPONSE_WATER_INTAKE_DTO.volume()))
                .andExpect(
                        jsonPath("$.content[0].volumeUnit")
                                .value(RESPONSE_WATER_INTAKE_DTO.volumeUnit().toString()))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.nextCursor").doesNotExist());
    }

    @Test
    void givenJwtWithoutRequiredScope_whenCreateWaterIntake_thenReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(
                        post("/api/v1/users/water-intakes")
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString())))
                                .content(objectMapper.writeValueAsString(WATER_INTAKE_DTO))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenJwtWithWrongScope_whenFindById_thenReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(
                        get("/api/v1/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString()))
                                                .authorities(
                                                        new SimpleGrantedAuthority(
                                                                "SCOPE_drinkwater:v1:user:profile:read")))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenJwtWithoutRequiredScope_whenDeleteWaterIntake_thenReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(
                        delete("/api/v1/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                                .with(
                                        jwt().jwt(
                                                        builder ->
                                                                builder.claim(
                                                                        "sub",
                                                                        USER_UUID.toString())))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US"))
                .andExpect(status().isForbidden());
    }

    private void expectWaterIntakeResponse(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.id").value(RESPONSE_WATER_INTAKE_DTO.id()))
                .andExpect(jsonPath("$.dateTimeUTC").value(DATE_TIME_UTC.toString()))
                .andExpect(jsonPath("$.volume").value(RESPONSE_WATER_INTAKE_DTO.volume()))
                .andExpect(
                        jsonPath("$.volumeUnit")
                                .value(RESPONSE_WATER_INTAKE_DTO.volumeUnit().toString()));
    }
}
