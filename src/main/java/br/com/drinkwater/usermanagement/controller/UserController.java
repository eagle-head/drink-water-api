package br.com.drinkwater.usermanagement.controller;

import br.com.drinkwater.usermanagement.dto.ResponseUserDTO;
import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseUserDTO> getCurrentUser(JwtAuthenticationToken token) {
        UUID publicId = UUID.fromString(token.getToken().getSubject());
        ResponseUserDTO userDTO = this.userService.getUserByPublicId(publicId);

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping
    public ResponseEntity<ResponseUserDTO> createUser(@Valid @RequestBody UserDTO userDTO,
                                                      JwtAuthenticationToken token) {
        UUID publicId = UUID.fromString(token.getToken().getSubject());
        ResponseUserDTO createdUser = this.userService.createUser(publicId, userDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<ResponseUserDTO> updateCurrentUser(JwtAuthenticationToken token,
                                                             @Valid @RequestBody UserDTO updateUserDTO) {
        UUID publicId = UUID.fromString(token.getToken().getSubject());
        ResponseUserDTO updatedUser = this.userService.updateUser(publicId, updateUserDTO);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCurrentUser(JwtAuthenticationToken token) {
        UUID publicId = UUID.fromString(token.getToken().getSubject());
        this.userService.deleteUser(publicId);

        return ResponseEntity.noContent().build();
    }
}
