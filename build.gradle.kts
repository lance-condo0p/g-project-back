plugins {
    kotlin("jvm") version "1.7.21"

    kotlin("plugin.serialization") version "1.9.10"
    // required for "ktor-serialization-kotlinx-json"
    id("io.ktor.plugin") version "2.3.9"
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:${project.property("logback_version")}")
    implementation(kotlin("stdlib"))

    testImplementation("io.ktor:ktor-server-test-host:${project.property("ktor_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${project.property("kotlin_version")}")
}

repositories {
    mavenCentral()
}