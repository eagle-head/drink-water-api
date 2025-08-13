package br.com.drinkwater.hydrationtracking.controller;

import br.com.drinkwater.core.PageResponse;
import br.com.drinkwater.hydrationtracking.dto.*;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users/water-intakes")
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

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWaterIntakeDTO> findById(@PathVariable Long id,
                                                           JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.waterIntakeService.findByIdAndUserId(id, user.getId());

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseWaterIntakeDTO> updateById(@PathVariable Long id,
                                                             @Valid @RequestBody WaterIntakeDTO updateDTO,
                                                             JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.waterIntakeService.update(id, updateDTO, user);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id, JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        this.waterIntakeService.deleteByIdAndUserId(id, user.getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<ResponseWaterIntakeDTO>> search(@Valid WaterIntakeFilterDTO filter,
                                                                       JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var response = this.waterIntakeService.search(filter, user);

        return ResponseEntity.ok(response);
    }
}
