package com.quizmaster.database;

/**
 * Manages database connection settings loaded from environment variables
 * with safe development defaults and protected credential handling.
 */
public class DatabaseConfig {

    public static final String ENV_DB_URL = "QUIZ_DB_URL";
    public static final String ENV_DB_USER = "QUIZ_DB_USER";
    public static final String ENV_DB_PASSWORD = "QUIZ_DB_PASSWORD";

    public static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/quizmaster_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String DEFAULT_USER = "root";
    public static final String DEFAULT_PASSWORD = "";

    private String url;
    private String username;
    private String password;

    public DatabaseConfig() {
        loadFromEnvironment();
    }

    public DatabaseConfig(String url, String username, String password) {
        this.url = url != null ? url : DEFAULT_URL;
        this.username = username != null ? username : DEFAULT_USER;
        this.password = password != null ? password : DEFAULT_PASSWORD;
    }

    public void loadFromEnvironment() {
        String envUrl = System.getenv(ENV_DB_URL);
        String envUser = System.getenv(ENV_DB_USER);
        String envPass = System.getenv(ENV_DB_PASSWORD);

        this.url = (envUrl != null && !envUrl.trim().isEmpty()) ? envUrl.trim() : DEFAULT_URL;
        this.username = (envUser != null && !envUser.trim().isEmpty()) ? envUser.trim() : DEFAULT_USER;
        this.password = (envPass != null) ? envPass : DEFAULT_PASSWORD;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a safe representation of the database credentials without exposing passwords.
     */
    public String getMaskedSummary() {
        return "DatabaseConfig[url=" + url + ", user=" + username + ", password=****]";
    }

    @Override
    public String toString() {
        return getMaskedSummary();
    }
}
