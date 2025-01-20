package br.com.drinkwater.hydrationtracking.controller;

import br.com.drinkwater.hydrationtracking.dto.*;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/waterintakes")
public class WaterIntakeController {

    private final WaterIntakeService waterIntakeService;
    private final UserService userService;

    public WaterIntakeController(WaterIntakeService waterIntakeService, UserService userService) {
        this.waterIntakeService = waterIntakeService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseWaterIntakeDTO> create(@Valid @RequestBody WaterIntakeDTO dto,
                                                         JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.waterIntakeService.create(dto, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<ResponseWaterIntakeDTO> findById(@PathVariable Long requestedId,
                                                           JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.waterIntakeService.findByIdAndUserId(requestedId, user.getId());

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<ResponseWaterIntakeDTO> updateById(@PathVariable Long requestedId,
                                                             @Valid @RequestBody WaterIntakeDTO updateDTO,
                                                             JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.waterIntakeService.update(requestedId, updateDTO, user);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long requestedId, JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        this.waterIntakeService.deleteByIdAndUserId(requestedId, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ResponseWaterIntakeDTO>> search(
            @Valid WaterIntakeFilterDTO filter,
            JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var result = this.waterIntakeService.search(filter, user);

        return ResponseEntity.ok(result);
    }
}
