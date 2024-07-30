package br.com.drinkwater.drinkwaterapi.usermanagement.controller;

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
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User savedUser = userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<User> findById(@PathVariable Long requestedId) {
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
    public ResponseEntity<User> update(@PathVariable Long requestedId, @Valid @RequestBody User user) {
        return userService.update(requestedId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
