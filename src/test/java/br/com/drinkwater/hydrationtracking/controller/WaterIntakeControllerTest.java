package br.com.drinkwater.hydrationtracking.controller;

import br.com.drinkwater.config.TestMessageSourceConfig;
import br.com.drinkwater.core.PageResponse;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import br.com.drinkwater.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static br.com.drinkwater.hydrationtracking.constants.WaterIntakeTestConstants.*;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER;
import static br.com.drinkwater.usermanagement.constants.UserTestConstants.USER_UUID;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WaterIntakeController.class)
@ActiveProfiles("test")
@Import(TestMessageSourceConfig.class)
public final class WaterIntakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @MockitoBean
    private WaterIntakeService waterIntakeService;

    @MockitoBean
    private UserService userService;

    @Test
    public void givenValidJwtTokenAndValidWaterIntakeDTO_whenCreateWaterIntake_thenReturnCreatedWaterIntakeResponse() throws Exception {

        when(this.userService.findByPublicId(USER_UUID)).thenReturn(USER);
        when(this.waterIntakeService.create(WATER_INTAKE_DTO, USER)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        mockMvc
                .perform(post("/users/water-intakes")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .content(this.objectMapper.writeValueAsString(WATER_INTAKE_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(RESPONSE_WATER_INTAKE_DTO.id()))
                .andExpect(jsonPath("$.dateTimeUTC")
                        .value(DATE_TIME_UTC.toString()))
                .andExpect(jsonPath("$.volume")
                        .value(RESPONSE_WATER_INTAKE_DTO.volume()))
                .andExpect(jsonPath("$.volumeUnit")
                        .value(RESPONSE_WATER_INTAKE_DTO.volumeUnit().toString()));
    }

    @Test
    public void givenValidJwtTokenAndExistingId_whenFindWaterIntakeById_thenReturnWaterIntakeResponse() throws Exception {
        when(this.userService.findByPublicId(USER_UUID)).thenReturn(USER);
        when(this.waterIntakeService.findByIdAndUserId(WATER_INTAKE_ID, USER.getId())).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        mockMvc
                .perform(get("/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(RESPONSE_WATER_INTAKE_DTO.id()))
                .andExpect(jsonPath("$.dateTimeUTC")
                        .value(DATE_TIME_UTC.toString()))
                .andExpect(jsonPath("$.volume")
                        .value(RESPONSE_WATER_INTAKE_DTO.volume()))
                .andExpect(jsonPath("$.volumeUnit")
                        .value(RESPONSE_WATER_INTAKE_DTO.volumeUnit().toString()));
    }

    @Test
    public void givenValidJwtTokenAndValidWaterIntakeDTO_whenUpdateWaterIntakeById_thenReturnUpdatedWaterIntakeResponse() throws Exception {
        when(this.userService.findByPublicId(USER_UUID)).thenReturn(USER);
        when(this.waterIntakeService.update(WATER_INTAKE_ID, WATER_INTAKE_DTO, USER)).thenReturn(RESPONSE_WATER_INTAKE_DTO);

        mockMvc
                .perform(put("/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .content(this.objectMapper.writeValueAsString(WATER_INTAKE_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(RESPONSE_WATER_INTAKE_DTO.id()))
                .andExpect(jsonPath("$.dateTimeUTC")
                        .value(DATE_TIME_UTC.toString()))
                .andExpect(jsonPath("$.volume")
                        .value(RESPONSE_WATER_INTAKE_DTO.volume()))
                .andExpect(jsonPath("$.volumeUnit")
                        .value(RESPONSE_WATER_INTAKE_DTO.volumeUnit().toString()));
    }

    @Test
    public void givenValidJwtTokenAndExistingId_whenDeleteWaterIntakeById_thenReturnNoContent() throws Exception {
        when(this.userService.findByPublicId(USER_UUID)).thenReturn(USER);
        doNothing().when(this.waterIntakeService).deleteByIdAndUserId(WATER_INTAKE_ID, USER.getId());

        mockMvc
                .perform(delete("/users/water-intakes/{requestedId}", WATER_INTAKE_ID)
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en-US"))
                .andExpect(status().isNoContent());

        verify(this.waterIntakeService, times(1))
                .deleteByIdAndUserId(WATER_INTAKE_ID, USER.getId());
    }

    @Test
    public void givenValidJwtTokenAndValidFilter_whenSearchWaterIntakes_thenReturnWaterIntakesPage() throws Exception {
        when(this.userService.findByPublicId(USER_UUID)).thenReturn(USER);

        // Criar instants sem milissegundos
        Instant startDate = Instant.now()
                .minus(7, ChronoUnit.DAYS)
                .truncatedTo(ChronoUnit.SECONDS);

        Instant endDate = Instant.now()
                .truncatedTo(ChronoUnit.SECONDS);

        var filterDTO = new WaterIntakeFilterDTO(
                startDate,
                endDate,
                100,
                2000,
                0,
                10,
                "dateTimeUTC",
                "DESC"
        );

        // Criar uma instância de PageResponse
        var pageResponse = new PageResponse<>(
                List.of(RESPONSE_WATER_INTAKE_DTO),
                1, // totalElements
                1, // totalPages
                1, // pageSize
                0, // pageNumber
                true, // first
                true  // last
        );

        when(this.waterIntakeService.search(filterDTO, USER)).thenReturn(pageResponse);

        mockMvc
                .perform(get("/users/water-intakes")
                        .with(jwt()
                                .jwt(builder -> builder.claim("sub", USER_UUID.toString()).build()))
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("minVolume", "100")
                        .param("maxVolume", "2000")
                        .param("page", "0")
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
                .andExpect(jsonPath("$.content[0].volume").value(RESPONSE_WATER_INTAKE_DTO.volume()))
                .andExpect(jsonPath("$.content[0].volumeUnit")
                        .value(RESPONSE_WATER_INTAKE_DTO.volumeUnit().toString()))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageSize").value(1));
    }
}