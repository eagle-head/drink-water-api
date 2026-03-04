package br.com.drinkwater.usermanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.drinkwater.config.MockContainersConfig;
import br.com.drinkwater.usermanagement.exception.UserNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest
@ActiveProfiles("it-no-containers")
@Import(MockContainersConfig.class)
@SqlGroup({
    @Sql(scripts = "/reset-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(scripts = "/insert-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
final class UserServiceCacheNoContainersIT {

    private static final UUID JOHN_DOE_PUBLIC_ID =
            UUID.fromString("fbc58717-5d48-4041-9f1c-257e8052428f");
    private static final Long JOHN_DOE_USER_ID = 1L;

    @Autowired private UserService userService;

    @Autowired private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        var cache = cacheManager.getCache("userIdByPublicId");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void givenExistingUser_whenResolveUserIdTwice_thenSecondCallUsesCache() {
        // Given - first call populates the cache
        Long firstResult = userService.resolveUserIdByPublicId(JOHN_DOE_PUBLIC_ID);

        // When - second call should hit cache
        Long secondResult = userService.resolveUserIdByPublicId(JOHN_DOE_PUBLIC_ID);

        // Then
        assertThat(firstResult).isEqualTo(JOHN_DOE_USER_ID);
        assertThat(secondResult).isEqualTo(JOHN_DOE_USER_ID);

        var cachedValue =
                cacheManager.getCache("userIdByPublicId").get(JOHN_DOE_PUBLIC_ID, Long.class);
        assertThat(cachedValue).isEqualTo(JOHN_DOE_USER_ID);
    }

    @Test
    void givenCachedUser_whenDeleteByPublicId_thenCacheEntryIsEvicted() {
        // Given - populate cache
        userService.resolveUserIdByPublicId(JOHN_DOE_PUBLIC_ID);
        assertThat(cacheManager.getCache("userIdByPublicId").get(JOHN_DOE_PUBLIC_ID)).isNotNull();

        // When
        userService.deleteByPublicId(JOHN_DOE_PUBLIC_ID);

        // Then
        assertThat(cacheManager.getCache("userIdByPublicId").get(JOHN_DOE_PUBLIC_ID)).isNull();
    }

    @Test
    void givenNonExistentUser_whenResolveUserId_thenThrowsUserNotFoundException() {
        // Given
        var unknownPublicId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        // When & Then
        assertThatThrownBy(() -> userService.resolveUserIdByPublicId(unknownPublicId))
                .isInstanceOf(UserNotFoundException.class);

        assertThat(cacheManager.getCache("userIdByPublicId").get(unknownPublicId)).isNull();
    }
}
