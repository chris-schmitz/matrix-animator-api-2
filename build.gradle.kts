import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
}

group = "com.lightinspiration"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// * Found this here:
// ? https://stackoverflow.com/a/71092054/1934903
// * but not sure if this is correct or necessary. The gradle jar
// * tasks exists even without this task def, but is it that the
// * below task creates the "uber" jar vs the default gradle jar task
// * making a smaller jar??
tasks.jar {
	archivesName = "coolapp"
	manifest.attributes["Main-Class"] = "com.lightinspiration.matrixanimatorapi.MatrixAnimatorApiApplication"
	manifest.attributes["Class-Path"] = configurations
		.runtimeClasspath
		.get()
		.joinToString(separator = " ") { file ->
			"libs/${file.name}"
		}
}