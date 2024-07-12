plugins {
    kotlin("jvm") version "1.7.21"

    kotlin("plugin.serialization") version "1.9.10"
    // required for "ktor-serialization-kotlinx-json"
//    id("io.ktor.plugin") version "2.3.9"
    id("io.ktor.plugin") version "2.3.12"
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-server-config-yaml:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-server-netty-jvm:${project.property("ktor_version")}")

    // Authentication (basic)
    implementation("io.ktor:ktor-server-auth:${project.property("ktor_version")}")

    // OpenAPI generation
    implementation("io.ktor:ktor-server-openapi:${project.property("ktor_version")}")

//    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm:${project.property("jackson_version")}")

    implementation("io.ktor:ktor-client-core:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-client-cio:${project.property("ktor_version")}")

    implementation("io.ktor:ktor-client-logging:${project.property("ktor_version")}")
    implementation("io.ktor:ktor-server-call-logging:${project.property("ktor_version")}")
    implementation("ch.qos.logback:logback-classic:${project.property("logback_version")}")
//    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${project.property("slf4j_version")}")
//    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")
    implementation(kotlin("stdlib"))

    implementation("com.assemblyai:assemblyai-java:${project.property("assemblyai_sdk_version")}")

    implementation("commons-codec:commons-codec:1.17.0")

    testImplementation("io.ktor:ktor-server-test-host:${project.property("ktor_version")}")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${project.property("kotlin_version")}")
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}