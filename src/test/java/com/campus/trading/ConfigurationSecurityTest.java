package com.campus.trading;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationSecurityTest {

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }

    @Test
    void applicationConfigurationUsesEnvironmentVariablesWithoutLegacySecrets() throws IOException {
        String application = read("src/main/resources/application.yml");
        String production = read("src/main/resources/application-prod.yml");
        String combined = application + "\n" + production;

        assertFalse(combined.contains("47.105.47.21"));
        assertFalse(combined.contains("campus-trading-jwt-secret-key-2024"));
        assertFalse(combined.contains("/opt/campus-trading"));
        assertFalse(combined.contains("password: 123456"));

        assertTrue(application.contains("${DB_PASSWORD:"));
        assertTrue(application.contains("${JWT_SECRET:"));
        assertTrue(application.contains("${UPLOAD_PATH:./uploads/}"));
        assertTrue(production.contains("${DB_PASSWORD}"));
        assertTrue(production.contains("${JWT_SECRET}"));
    }

    @Test
    void initializationSqlDoesNotContainLegacyInfrastructureOrRealContactData() throws IOException {
        String sql = read("src/main/resources/db/init.sql");

        assertFalse(sql.contains("47.105.47.21"));
        assertFalse(sql.contains("/opt/campus-trading"));
        assertFalse(sql.matches("(?s).*1[3-9]\\d{9}.*"));
        assertTrue(sql.contains("campus_market"));
    }
}
