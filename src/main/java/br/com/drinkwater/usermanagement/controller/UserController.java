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

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.userMapper.toDTO(user);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO createDTO, JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var email = token.getToken().getClaimAsString("email");
        var user = this.userMapper.toEntity(createDTO, publicId, email);
        var createdUser = this.userService.create(user);
        var responseDTO = this.userMapper.toDTO(createdUser);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> findUser(JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var user = this.userService.findByPublicId(publicId);
        var responseDTO = this.userMapper.toDTO(user);

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UserUpdateDTO userUpdateDTO, JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        var email = token.getToken().getClaimAsString("email");
        var updatedUser = this.userService.update(publicId, userUpdateDTO, email);
        var responseDTO = this.userMapper.toDTO(updatedUser);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(JwtAuthenticationToken token) {
        var publicId = UUID.fromString(token.getToken().getSubject());
        this.userService.delete(publicId);

        return ResponseEntity.noContent().build();
    }
}
