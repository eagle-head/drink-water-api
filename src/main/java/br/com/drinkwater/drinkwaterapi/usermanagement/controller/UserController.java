package br.com.drinkwater.drinkwaterapi.usermanagement.controller;

import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserCreateDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.drinkwaterapi.usermanagement.model.User;
import br.com.drinkwater.drinkwaterapi.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO savedUser = userService.create(userCreateDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long requestedId) {
        return userService.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> delete(@PathVariable Long requestedId) {
        userService.deleteById(requestedId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long requestedId, @Valid @RequestBody User user) {
        return userService.update(requestedId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
