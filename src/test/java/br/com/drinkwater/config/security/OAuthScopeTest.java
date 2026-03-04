package br.com.drinkwater.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

final class OAuthScopeTest {

    @Test
    void allConstantsStartWithScopePrefix() {
        assertThat(OAuthScope.USER_PROFILE_READ).startsWith("SCOPE_");
        assertThat(OAuthScope.USER_PROFILE_CREATE).startsWith("SCOPE_");
        assertThat(OAuthScope.USER_PROFILE_UPDATE).startsWith("SCOPE_");
        assertThat(OAuthScope.USER_PROFILE_DELETE).startsWith("SCOPE_");
        assertThat(OAuthScope.WATERINTAKE_ENTRY_READ).startsWith("SCOPE_");
        assertThat(OAuthScope.WATERINTAKE_ENTRY_CREATE).startsWith("SCOPE_");
        assertThat(OAuthScope.WATERINTAKE_ENTRY_UPDATE).startsWith("SCOPE_");
        assertThat(OAuthScope.WATERINTAKE_ENTRY_DELETE).startsWith("SCOPE_");
        assertThat(OAuthScope.WATERINTAKE_ENTRIES_SEARCH).startsWith("SCOPE_");
        assertThat(OAuthScope.ADMIN_CONFIG_MANAGE).startsWith("SCOPE_");
        assertThat(OAuthScope.ADMIN_CONFIG_READ).startsWith("SCOPE_");
    }

    @Test
    void allConstantsFollowNamingConvention() {
        assertThat(OAuthScope.USER_PROFILE_READ).isEqualTo("SCOPE_drinkwater:v1:user:profile:read");
        assertThat(OAuthScope.WATERINTAKE_ENTRY_CREATE)
                .isEqualTo("SCOPE_drinkwater:v1:waterintake:entry:create");
        assertThat(OAuthScope.ADMIN_CONFIG_MANAGE)
                .isEqualTo("SCOPE_drinkwater:v1:admin:config:manage");
    }

    @Test
    void constructorIsPrivate() throws NoSuchMethodException {
        Constructor<OAuthScope> constructor = OAuthScope.class.getDeclaredConstructor();
        assertThat(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }
}
