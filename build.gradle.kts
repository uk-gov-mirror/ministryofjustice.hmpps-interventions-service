plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "1.1.1"
  kotlin("plugin.spring") version "1.4.10"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}
