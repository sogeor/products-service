plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sogeor"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    compileOnly("org.jetbrains:annotations:${findProperty("org.jetbrains.annotations.version")}")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation(
        "org.springdoc:springdoc-openapi-starter-webmvc-ui:${
            findProperty("org.springdoc.springdoc-openapi-starter-webmvc-ui.version")
        }"
    )

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor(
        "org.projectlombok:lombok-mapstruct-binding:${findProperty("org.projectlombok.lombok-mapstruct-binding")}"
    )

    runtimeOnly("org.postgresql:postgresql")
    implementation("com.zaxxer:HikariCP:${findProperty("com.zaxxer.HikariCP")}")
    implementation("org.mapstruct:mapstruct:${findProperty("org.mapstruct.mapstruct")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${findProperty("org.mapstruct.mapstruct")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.wrapper {
    gradleVersion = "9.2.1"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
