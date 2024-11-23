package br.com.drinkwater.usermanagement.controller;

import br.com.drinkwater.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.dto.UserUpdateDTO;
import br.com.drinkwater.usermanagement.mapper.UserMapper;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO createDTO) {
        var user = this.userMapper.toEntity(createDTO);
        var createdUser = this.userService.create(user, createDTO.password());
        var responseDTO = this.userMapper.toDTO(createdUser);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> findByEmail(JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var user = this.userService.findByEmail(email);
        var responseDTO = this.userMapper.toDTO(user);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                                  JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        var updatedUser = this.userService.update(email, userUpdateDTO);
        var responseDTO = this.userMapper.toDTO(updatedUser);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(JwtAuthenticationToken token) {
        var email = token.getToken().getClaimAsString("preferred_username");
        this.userService.delete(email);
        return ResponseEntity.noContent().build();
    }
}
