package br.com.drinkwater.hydrationtracking.controller;

import br.com.drinkwater.hydrationtracking.dto.WaterIntakeCreateDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeResponseDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeUpdateDTO;
import br.com.drinkwater.hydrationtracking.mapper.WaterIntakeMapper;
import br.com.drinkwater.hydrationtracking.model.WaterIntake;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import br.com.drinkwater.usermanagement.model.User;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<WaterIntakeResponseDTO>> findAll(JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);

        List<WaterIntake> waterIntakes = this.waterIntakeService.findAllByUserId(user.getId());
        List<WaterIntakeResponseDTO> responseDTOList = waterIntakes
                .stream()
                .map(this.waterIntakeMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOList);
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
