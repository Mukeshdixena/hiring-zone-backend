package com.hiringzone.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final String PROPERTY_SOURCE_NAME = "hiringZoneDotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> properties = loadDotenv(Path.of(".env"));
        String dbUrl = firstText((String) properties.get("DB_URL"), environment.getProperty("DB_URL"));

        if (StringUtils.hasText(dbUrl)) {
            applyDatabaseUrl(dbUrl.trim(), properties);
        }

        if (!properties.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }

    private Map<String, Object> loadDotenv(Path path) {
        Map<String, Object> properties = new LinkedHashMap<>();

        if (!Files.exists(path)) {
            return properties;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separator = trimmed.indexOf('=');
                if (separator <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, separator).trim();
                String value = stripQuotes(trimmed.substring(separator + 1).trim());
                if (StringUtils.hasText(key) && StringUtils.hasText(value)) {
                    properties.put(key, value);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read .env file", ex);
        }

        return properties;
    }

    private void applyDatabaseUrl(String rawUrl, Map<String, Object> properties) {
        String postgresUrl = rawUrl.startsWith("jdbc:") ? rawUrl.substring("jdbc:".length()) : rawUrl;

        if (!isPostgresUrl(postgresUrl)) {
            properties.put("spring.datasource.url", rawUrl);
            return;
        }

        URI uri = URI.create(normalizePostgresScheme(postgresUrl));
        String host = uri.getHost();

        if (!StringUtils.hasText(host)) {
            properties.put("spring.datasource.url", rawUrl.startsWith("jdbc:") ? rawUrl : "jdbc:" + rawUrl);
            return;
        }

        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://").append(host);
        if (uri.getPort() > 0) {
            jdbcUrl.append(':').append(uri.getPort());
        }

        String path = uri.getRawPath();
        jdbcUrl.append(StringUtils.hasText(path) ? path : "/");

        String query = uri.getRawQuery();
        if (StringUtils.hasText(query)) {
            jdbcUrl.append('?').append(query);
        }

        properties.put("spring.datasource.url", jdbcUrl.toString());
        applyUserInfo(uri.getRawUserInfo(), properties);
    }

    private void applyUserInfo(String userInfo, Map<String, Object> properties) {
        if (!StringUtils.hasText(userInfo)) {
            return;
        }

        int separator = userInfo.indexOf(':');
        String username = separator >= 0 ? userInfo.substring(0, separator) : userInfo;
        String password = separator >= 0 ? userInfo.substring(separator + 1) : "";

        if (StringUtils.hasText(username)) {
            properties.put("spring.datasource.username", decode(username));
        }
        if (StringUtils.hasText(password)) {
            properties.put("spring.datasource.password", decode(password));
        }
    }

    private boolean isPostgresUrl(String value) {
        return value.startsWith("postgresql://") || value.startsWith("postgres://");
    }

    private String normalizePostgresScheme(String value) {
        if (value.startsWith("postgres://")) {
            return "postgresql://" + value.substring("postgres://".length());
        }
        return value;
    }

    private String firstText(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        return second;
    }

    private String stripQuotes(String value) {
        if (value.length() < 2) {
            return value;
        }

        boolean singleQuoted = value.startsWith("'") && value.endsWith("'");
        boolean doubleQuoted = value.startsWith("\"") && value.endsWith("\"");
        return singleQuoted || doubleQuoted ? value.substring(1, value.length() - 1) : value;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
