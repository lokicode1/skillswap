package com.skillswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkillSwapApplication {
  public static void main(String[] args) {
    String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
    if (datasourceUrl != null && !datasourceUrl.isBlank()) {
      String normalized = datasourceUrl.trim();
      if (normalized.startsWith("postgres://")) {
        normalized = "jdbc:postgresql://" + normalized.substring("postgres://".length());
      } else if (normalized.startsWith("postgresql://")) {
        normalized = "jdbc:postgresql://" + normalized.substring("postgresql://".length());
      }
      if (!normalized.startsWith("jdbc:")) {
        String host = getenvOrDefault("SKILLSWAP_DB_HOST", "localhost");
        String port = getenvOrDefault("SKILLSWAP_DB_PORT", "5433");
        String db = getenvOrDefault("SKILLSWAP_DB_NAME", "skillswap");
        normalized = "jdbc:postgresql://" + host + ":" + port + "/" + db;
      }
      System.setProperty("spring.datasource.url", normalized);
    } else {
      String host = getenvOrDefault("SKILLSWAP_DB_HOST", "localhost");
      String port = getenvOrDefault("SKILLSWAP_DB_PORT", "5433");
      String db = getenvOrDefault("SKILLSWAP_DB_NAME", "skillswap");
      System.setProperty("spring.datasource.url", "jdbc:postgresql://" + host + ":" + port + "/" + db);
    }
    SpringApplication.run(SkillSwapApplication.class, args);
  }

  private static String getenvOrDefault(String key, String fallback) {
    String val = System.getenv(key);
    return val == null || val.isBlank() ? fallback : val;
  }
}

