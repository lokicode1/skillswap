package com.skillswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkillSwapApplication {
  public static void main(String[] args) {
    String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
    if (datasourceUrl != null && datasourceUrl.startsWith("postgres://")) {
      // Render may expose Postgres URL as postgres://... for app env vars.
      // Spring Boot's DataSource requires a JDBC URL.
      String jdbcUrl = "jdbc:postgresql://" + datasourceUrl.substring("postgres://".length());
      System.setProperty("spring.datasource.url", jdbcUrl);
    }
    SpringApplication.run(SkillSwapApplication.class, args);
  }
}

