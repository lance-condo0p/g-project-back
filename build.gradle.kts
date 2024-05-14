plugins {
    kotlin("jvm") version "1.7.21"

    kotlin("plugin.serialization") version "1.9.10"
    // required for "ktor-serialization-kotlinx-json"
    id("io.ktor.plugin") version "2.3.9"
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-netty-jvm")

//    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm:${project.property("jackson_version")}")

    implementation("io.ktor:ktor-client-core:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-client-content-negotiation-jvm")
    implementation("io.ktor:ktor-client-cio:${project.property("ktor_version")}")

    implementation("io.ktor:ktor-client-logging:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-server-call-logging:${project.property("ktor_version")}")
    implementation("ch.qos.logback:logback-classic:${project.property("logback_version")}")
    implementation(kotlin("stdlib"))

    testImplementation("io.ktor:ktor-server-test-host:${project.property("ktor_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${project.property("kotlin_version")}")
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}