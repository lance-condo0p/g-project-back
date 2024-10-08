// TODO: switch to https://splitties.github.io/refreshVersions/ or not?
val kotlin_version: String by project
val ktor_version: String by project
val jackson_version: String by project
val logback_version: String by project
val assemblyai_sdk_version: String by project
val apache_commons_codec: String by project
val mockk_version: String by project

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.ktor.plugin") version "2.3.9"
}

group = "jfm.we"
version = "0.0.5"

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    // Authentication (basic)
    implementation("io.ktor:ktor-server-auth:$ktor_version")

    // OpenAPI generation
    implementation("io.ktor:ktor-server-openapi:$ktor_version")

    implementation("io.ktor:ktor-serialization-jackson-jvm:$jackson_version")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation(kotlin("stdlib"))

    implementation("commons-codec:commons-codec:$apache_commons_codec")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:$mockk_version")
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    fatJar {
        archiveFileName.set("jfm.we.g-project-back-$version.jar")
    }
}

tasks.test {
    useJUnitPlatform {
        filter {
            includeTestsMatching("*Test")
            excludeTestsMatching("*IT")
        }
    }
}

val integrationTest = tasks.register<Test>("integrationTest") {
    useJUnitPlatform {
        filter {
            includeTestsMatching("*IT")
        }
    }
}

tasks.named("check") {
    dependsOn(integrationTest)
}