package br.com.drinkwater.hydrationtracking.controller;

import br.com.drinkwater.hydrationtracking.dto.*;
import br.com.drinkwater.hydrationtracking.mapper.WaterIntakeMapper;
import br.com.drinkwater.hydrationtracking.model.VolumeUnit;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users/waterintakes")
public class WaterIntakeController {

    private final WaterIntakeService waterIntakeService;
    private final UserService userService;
    private final WaterIntakeMapper waterIntakeMapper;

    public WaterIntakeController(WaterIntakeService waterIntakeService, UserService userService,
                                 WaterIntakeMapper waterIntakeMapper) {
        this.waterIntakeService = waterIntakeService;
        this.userService = userService;
        this.waterIntakeMapper = waterIntakeMapper;
    }

    @PostMapping
    public ResponseEntity<WaterIntakeResponseDTO> create(@Valid @RequestBody WaterIntakeCreateDTO dto,
                                                         JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);

        var waterIntake = this.waterIntakeMapper.toEntity(dto, user);
        var savedWaterIntake = this.waterIntakeService.save(waterIntake);
        var responseDTO = this.waterIntakeMapper.toDto(savedWaterIntake);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<WaterIntakeResponseDTO> findById(@PathVariable Long requestedId,
                                                           JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);
        var waterIntake = this.waterIntakeService.findByIdAndUserId(requestedId, user.getId());
        var responseDTO = this.waterIntakeMapper.toDto(waterIntake);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<PaginatedWaterIntakeResponseDTO> findAll(@ModelAttribute @Valid WaterIntakeFilterDTO filterDTO,
                                                                                    JwtAuthenticationToken token) {

        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);

        Sort sort = filterDTO.direction().equalsIgnoreCase("asc")
                ? Sort.by(filterDTO.sortBy()).ascending()
                : Sort.by(filterDTO.sortBy()).descending();

        PageRequest pageRequest = PageRequest.of(filterDTO.page(), filterDTO.size(), sort);

        Page<WaterIntake> waterIntakePage = this.waterIntakeService.findAllByUserIdWithFilters(
                user.getId(),
                filterDTO.startDate(),
                filterDTO.endDate(),
                filterDTO.minVolume(),
                filterDTO.maxVolume(),
                filterDTO.volumeUnit(),
                pageRequest);

        var responseDTO = this.waterIntakeMapper.toPaginatedDto(waterIntakePage);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<WaterIntakeResponseDTO> updateById(@PathVariable Long requestedId,
                                                             @Valid @RequestBody WaterIntakeUpdateDTO updateDTO,
                                                             JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);

        var waterIntake = this.waterIntakeService.findByIdAndUserId(requestedId, user.getId());
        this.waterIntakeMapper.toEntity(updateDTO, waterIntake);
        var updatedWaterIntake = this.waterIntakeService.save(waterIntake);
        var responseDTO = this.waterIntakeMapper.toDto(updatedWaterIntake);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long requestedId, JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);

        this.waterIntakeService.deleteByIdAndUserId(requestedId, user.getId());

        return ResponseEntity.noContent().build();
    }
}
