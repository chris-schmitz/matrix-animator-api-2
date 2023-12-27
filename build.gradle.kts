import groovy.lang.MissingPropertyException
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

val environmentVariables = readDotEnvFile()

group = "com.lightinspiration"
version = environmentVariables.getValue("JAR_VERSION")

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.20")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.3")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.2.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.liquibase:liquibase-core:4.25.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")

        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true

        showStandardStreams = false
    }
}



tasks {
    val dockerComposeUp by creating(Task::class) {
        description = "Builds app, spins up docker containers, and launches the API."
        dependsOn(assemble)

        doLast {
            val command = "JAR_VERSION=$version; echo \$JAR_VERSION; docker-compose up --build --detach"
            project.exec {
                commandLine("sh", "-c", command)
            }
        }
    }
    val dockerComposeDown by creating(Task::class) {
        description = "Pulls a running detached docker servies down."
        doLast {
            project.exec {
                commandLine("sh", "-c", "docker-compose down")
            }
        }
    }
}

// !--------Build Utilities---------! //
// * I tried to pull these out to a class, but when I did intellij was _not happy_ about it.
data class EnvironmentVariable(
    val name: String,
    val value: String
)

fun List<EnvironmentVariable>.getValue(name: String): String {
    return this.firstOrNull { it.name == name }?.value
        ?: throw MissingPropertyException("The $name variable is missing from the .env file")
}

fun readDotEnvFile(): List<EnvironmentVariable> {
    return File(".env")
        .bufferedReader()
        .use { it.readLines() }
        .map { it.split("=") }
        .map { EnvironmentVariable(it[0], it[1]) }
}
