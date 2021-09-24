package com.mesim.sc.constants;

public final class SecurityConstants {

    public static final String AUTH_LOGIN_URL = "/api/auth/login";
    public static final String AUTH_ATTEMPT_URL = "/api/auth/attempt";

    // Signing key for HS512 algorithm
    public static final String JWT_SECRET = "cvijk6AhxiiHAcrbBQFwVLlVxaLYN87ZOZmP-NrA0JRkMSQGp6AOZOr7YANZHv1CzgDq7mjIUkTMQifCFdOrpg";

    // JWT token defaults
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secure-api";
    public static final String TOKEN_AUDIENCE = "secure-app";

    // Reset Password
    public static final String RESET_PASSWORD = "Qwer1234!@#$";

    private SecurityConstants() {
        throw new IllegalStateException("Cannot create instance of static util class");

    }
}
