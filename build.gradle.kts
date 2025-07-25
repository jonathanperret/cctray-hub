import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlin.jvm") version "1.9.25"
	id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
	id("com.adarshr.test-logger") version "4.0.0"
//	id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "name.hennr"

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.session:spring-session-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// logging
	implementation("ch.qos.logback:logback-classic")
	implementation("ch.qos.logback:logback-access:1.5.12")
	implementation("net.logstash.logback:logstash-logback-encoder:8.1")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	// caching
	implementation("com.github.ben-manes.caffeine:caffeine")

	// metrics
	implementation("io.micrometer:micrometer-registry-prometheus")

	testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.projectreactor:reactor-test")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict", "-Xbackend-threads=2"
        )
    }
}

tasks.withType<Test> {
	useJUnitPlatform()
}

testlogger {
	theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA_PARALLEL
}
