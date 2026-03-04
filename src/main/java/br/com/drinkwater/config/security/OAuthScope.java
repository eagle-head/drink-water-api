package br.com.drinkwater.config.security;

/**
 * Centralizes all OAuth scope constants used by {@code @PreAuthorize} annotations. Each constant
 * includes the {@code SCOPE_} prefix expected by Spring Security when mapping JWT scope claims to
 * granted authorities.
 */
public final class OAuthScope {

    public static final String USER_PROFILE_READ = "SCOPE_drinkwater:v1:user:profile:read";
    public static final String USER_PROFILE_CREATE = "SCOPE_drinkwater:v1:user:profile:create";
    public static final String USER_PROFILE_UPDATE = "SCOPE_drinkwater:v1:user:profile:update";
    public static final String USER_PROFILE_DELETE = "SCOPE_drinkwater:v1:user:profile:delete";

    public static final String WATERINTAKE_ENTRY_READ =
            "SCOPE_drinkwater:v1:waterintake:entry:read";
    public static final String WATERINTAKE_ENTRY_CREATE =
            "SCOPE_drinkwater:v1:waterintake:entry:create";
    public static final String WATERINTAKE_ENTRY_UPDATE =
            "SCOPE_drinkwater:v1:waterintake:entry:update";
    public static final String WATERINTAKE_ENTRY_DELETE =
            "SCOPE_drinkwater:v1:waterintake:entry:delete";
    public static final String WATERINTAKE_ENTRIES_SEARCH =
            "SCOPE_drinkwater:v1:waterintake:entries:search";

    public static final String ADMIN_CONFIG_MANAGE = "SCOPE_drinkwater:v1:admin:config:manage";
    public static final String ADMIN_CONFIG_READ = "SCOPE_drinkwater:v1:admin:config:read";

    private OAuthScope() {}
}
