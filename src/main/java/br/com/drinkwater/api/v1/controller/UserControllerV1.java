package br.com.drinkwater.api.v1.controller;

import br.com.drinkwater.api.versioning.ApiVersion;
import br.com.drinkwater.config.security.AuthenticatedUser;
import br.com.drinkwater.exception.ProblemDetailSchema;
import br.com.drinkwater.exception.ScopeProblemDetailSchema;
import br.com.drinkwater.exception.ValidationProblemDetailSchema;
import br.com.drinkwater.usermanagement.dto.UserDTO;
import br.com.drinkwater.usermanagement.dto.UserResponseDTO;
import br.com.drinkwater.usermanagement.service.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user profile management.
 *
 * <p>Provides CRUD endpoints for the authenticated user's profile at {@code /api/v1/users}. All
 * operations are scoped to the currently authenticated user identified by their Keycloak public ID.
 */
@RestController
@RequestMapping("/api/v1/users")
@ApiVersion("v1")
@Tag(name = "Users", description = "User profile management")
public class UserControllerV1 {

    private static final Logger log = LoggerFactory.getLogger(UserControllerV1.class);

    private final UserService userService;

    public UserControllerV1(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return the user profile wrapped in a 200 OK response
     * @throws br.com.drinkwater.usermanagement.exception.UserNotFoundException if no user exists
     *     with the given public ID
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:read')")
    @RateLimiter(name = "user-api")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT token",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Insufficient scope",
            content = @Content(schema = @Schema(implementation = ScopeProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticatedUser UUID publicId) {
        log.debug("GET /api/v1/users/me for publicId: {}", publicId);
        var userDTO = this.userService.getUserByPublicId(publicId);
        return ResponseEntity.ok(userDTO);
    }

    /**
     * Creates a new user profile for the authenticated user.
     *
     * @param userDTO the validated user data
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return the created user profile wrapped in a 201 Created response
     * @throws br.com.drinkwater.usermanagement.exception.UserAlreadyExistsException if a profile
     *     already exists for this public ID
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:create')")
    @RateLimiter(name = "user-api")
    @Operation(
            summary = "Create user profile",
            description = "Creates a new user profile for the authenticated user")
    @ApiResponse(responseCode = "201", description = "User profile created successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content =
                    @Content(
                            schema = @Schema(implementation = ValidationProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT token",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Insufficient scope",
            content = @Content(schema = @Schema(implementation = ScopeProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserDTO userDTO, @AuthenticatedUser UUID publicId) {
        log.info("POST /api/v1/users for publicId: {}", publicId);
        var createdUser = this.userService.createUser(publicId, userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Updates the profile of the currently authenticated user.
     *
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @param updateUserDTO the validated updated user data
     * @return the updated user profile wrapped in a 200 OK response
     * @throws br.com.drinkwater.usermanagement.exception.UserNotFoundException if no user exists
     *     with the given public ID
     */
    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:update')")
    @RateLimiter(name = "user-api")
    @Operation(
            summary = "Update user profile",
            description = "Updates the profile of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "User profile updated successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content =
                    @Content(
                            schema = @Schema(implementation = ValidationProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT token",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Insufficient scope",
            content = @Content(schema = @Schema(implementation = ScopeProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<UserResponseDTO> updateCurrentUser(
            @AuthenticatedUser UUID publicId, @Valid @RequestBody UserDTO updateUserDTO) {
        log.info("PUT /api/v1/users for publicId: {}", publicId);
        var updatedUser = this.userService.updateUser(publicId, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes the profile of the currently authenticated user. The operation is idempotent: if the
     * user does not exist, it returns 204 No Content without error.
     *
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return an empty 204 No Content response
     */
    @DeleteMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:user:profile:delete')")
    @RateLimiter(name = "user-api")
    @Operation(
            summary = "Delete user profile",
            description =
                    "Deletes the authenticated user's profile. Idempotent: returns 204 even if"
                            + " the user does not exist")
    @ApiResponse(responseCode = "204", description = "User profile deleted successfully")
    @ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT token",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Insufficient scope",
            content = @Content(schema = @Schema(implementation = ScopeProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticatedUser UUID publicId) {
        log.info("DELETE /api/v1/users for publicId: {}", publicId);
        this.userService.deleteByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }
}
