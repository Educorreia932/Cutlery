plugins {
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.serialization") version "2.1.0"
}

kotlin {
	jvmToolchain(23)
}

group = "org.cutlery"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))

	// Kotlinx Serialization
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
	
	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.18.+")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.+")
}

tasks.test {
	useJUnitPlatform()
}