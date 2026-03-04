package br.com.drinkwater.api.v1.controller;

import br.com.drinkwater.api.versioning.ApiVersion;
import br.com.drinkwater.config.security.AuthenticatedUser;
import br.com.drinkwater.core.CursorPageResponse;
import br.com.drinkwater.exception.ProblemDetailSchema;
import br.com.drinkwater.exception.ScopeProblemDetailSchema;
import br.com.drinkwater.exception.ValidationProblemDetailSchema;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeFilterDTO;
import br.com.drinkwater.hydrationtracking.dto.WaterIntakeResponseDTO;
import br.com.drinkwater.hydrationtracking.service.WaterIntakeService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for water intake tracking.
 *
 * <p>Provides CRUD and search endpoints for water intake records at {@code
 * /api/v1/users/water-intakes}. All operations are scoped to the currently authenticated user and
 * support cursor-based pagination for search.
 */
@RestController
@RequestMapping("/api/v1/users/water-intakes")
@ApiVersion("v1")
@Tag(name = "Water Intakes", description = "Water intake tracking and search")
public class WaterIntakeControllerV1 {

    private static final Logger log = LoggerFactory.getLogger(WaterIntakeControllerV1.class);

    private final WaterIntakeService waterIntakeService;

    public WaterIntakeControllerV1(WaterIntakeService waterIntakeService) {
        this.waterIntakeService = waterIntakeService;
    }

    /**
     * Creates a new water intake record for the authenticated user.
     *
     * @param dto the validated water intake data
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return the created water intake wrapped in a 201 Created response
     * @throws br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException if a record
     *     with the same date/time already exists for this user
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:waterintake:entry:create')")
    @RateLimiter(name = "waterintake-api")
    @Operation(
            summary = "Create water intake record",
            description = "Creates a new water intake record for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Water intake created successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation error or duplicate date/time",
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
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<WaterIntakeResponseDTO> create(
            @Valid @RequestBody WaterIntakeDTO dto, @AuthenticatedUser UUID publicId) {
        log.info("POST /api/v1/users/water-intakes for user: {}", publicId);
        var responseDTO = this.waterIntakeService.create(dto, publicId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Retrieves a single water intake record by its ID.
     *
     * @param id the water intake record ID
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return the water intake record wrapped in a 200 OK response
     * @throws br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException if no
     *     record exists with the given ID for this user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:waterintake:entry:read')")
    @RateLimiter(name = "waterintake-api")
    @Operation(
            summary = "Get water intake by ID",
            description = "Retrieves a single water intake record by its ID")
    @ApiResponse(responseCode = "200", description = "Water intake retrieved successfully")
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
            description = "Water intake not found",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<WaterIntakeResponseDTO> findById(
            @Parameter(description = "Water intake record ID", example = "1") @PathVariable Long id,
            @AuthenticatedUser UUID publicId) {
        log.debug("GET /api/v1/users/water-intakes/{} for user: {}", id, publicId);
        var responseDTO = this.waterIntakeService.findByIdAndUserId(id, publicId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Updates an existing water intake record.
     *
     * @param id the water intake record ID
     * @param updateDTO the validated updated water intake data
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return the updated water intake record wrapped in a 200 OK response
     * @throws br.com.drinkwater.hydrationtracking.exception.WaterIntakeNotFoundException if no
     *     record exists with the given ID for this user
     * @throws br.com.drinkwater.hydrationtracking.exception.DuplicateDateTimeException if the
     *     updated date/time conflicts with another record for this user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:waterintake:entry:update')")
    @RateLimiter(name = "waterintake-api")
    @Operation(
            summary = "Update water intake by ID",
            description = "Updates an existing water intake record")
    @ApiResponse(responseCode = "200", description = "Water intake updated successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation error or duplicate date/time",
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
            description = "Water intake not found",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<WaterIntakeResponseDTO> updateById(
            @Parameter(description = "Water intake record ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody WaterIntakeDTO updateDTO,
            @AuthenticatedUser UUID publicId) {
        log.info("PUT /api/v1/users/water-intakes/{} for user: {}", id, publicId);
        var responseDTO = this.waterIntakeService.update(id, updateDTO, publicId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Deletes a water intake record. The operation is idempotent: if the record does not exist, it
     * returns 204 No Content without error.
     *
     * @param id the water intake record ID
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return an empty 204 No Content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:waterintake:entry:delete')")
    @RateLimiter(name = "waterintake-api")
    @Operation(
            summary = "Delete water intake by ID",
            description =
                    "Deletes a water intake record. Idempotent: returns 204 even if the record"
                            + " does not exist")
    @ApiResponse(responseCode = "204", description = "Water intake deleted successfully")
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
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Water intake record ID", example = "1") @PathVariable Long id,
            @AuthenticatedUser UUID publicId) {
        log.info("DELETE /api/v1/users/water-intakes/{} for user: {}", id, publicId);
        this.waterIntakeService.deleteByIdAndUserId(id, publicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches water intake records with filtering and cursor-based pagination.
     *
     * @param filter the validated search criteria including date range, volume range, sort, and
     *     cursor
     * @param publicId the Keycloak public ID extracted from the JWT token
     * @return a cursor-paginated page of matching water intake records wrapped in a 200 OK response
     */
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_drinkwater:v1:waterintake:entries:search')")
    @RateLimiter(name = "waterintake-search")
    @Operation(
            summary = "Search water intake records",
            description =
                    "Searches water intake records with filtering and cursor-based pagination")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid filter parameters",
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
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
    public ResponseEntity<CursorPageResponse<WaterIntakeResponseDTO>> search(
            @Valid WaterIntakeFilterDTO filter, @AuthenticatedUser UUID publicId) {
        log.debug("GET /api/v1/users/water-intakes (search) for user: {}", publicId);
        var response = this.waterIntakeService.search(filter, publicId);
        return ResponseEntity.ok(response);
    }
}
