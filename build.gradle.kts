plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.1.1"
  kotlin("plugin.spring") version "1.4.31"
  id("org.jetbrains.kotlin.plugin.jpa") version "1.4.31"
}

repositories {
  jcenter()
  mavenCentral()
}

configurations {
  testImplementation {
    exclude(group = "org.junit.vintage")
  }
}

tasks {
  test {
    useJUnitPlatform {
      exclude("**/*PactTest*")
    }
  }

  register<Test>("pactTestPublish") {
    description = "Run and publish Pact provider tests"
    group = "verification"

    systemProperty("pact.provider.tag", System.getenv("PACT_PROVIDER_TAG"))
    systemProperty("pact.provider.version", System.getenv("PACT_PROVIDER_VERSION"))
    systemProperty("pact.verifier.publishResults", System.getenv("PACT_PUBLISH_RESULTS") ?: "false")

    useJUnitPlatform {
      include("**/*PactTest*")
    }
  }
}

dependencies {
  // monitoring and logging
  implementation("io.sentry:sentry-spring-boot-starter:4.3.0")
  implementation("io.sentry:sentry-logback:4.3.0")
  implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")

  // openapi
  implementation("org.springdoc:springdoc-openapi-ui:1.5.5")

  // notifications
  implementation("uk.gov.service.notify:notifications-java-client:3.17.0-RELEASE")
  implementation("software.amazon.awssdk:sns:2.16.9")

  // security
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("com.nimbusds:oauth2-oidc-sdk:9.3.1")

  // database
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.hibernate:hibernate-core:5.4.28.Final")
  implementation("com.vladmihalcea:hibernate-types-52:2.10.3")
  runtimeOnly("org.flywaydb:flyway-core")
  runtimeOnly("org.postgresql:postgresql")

  testImplementation("au.com.dius.pact.provider:junit5spring:4.2.0")
  testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
}
