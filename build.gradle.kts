plugins {
    java
    application
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.home"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Solon BOM keeps all org.noear versions aligned
    implementation(platform("org.noear:solon-parent:4.1.1"))

    implementation("org.noear:solon-web") {
        // Replaced by Jackson below
        exclude(group = "org.noear", module = "solon-serialization-snack4")
    }
    implementation("org.noear:solon-serialization-jackson")
    implementation("org.noear:solon-logging-logback")
    implementation("org.noear:solon-health")
    implementation("org.noear:solon-openapi3-knife4j")

    implementation("org.noear:wood-solon-plugin")
    implementation("com.zaxxer:HikariCP:6.3.0")
    runtimeOnly("org.postgresql:postgresql:42.7.14")
}

application {
    mainClass = "com.home.App"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // Solon binds request params by name, so keep parameter names in bytecode
    options.compilerArgs.add("-parameters")
}

tasks.shadowJar {
    archiveFileName = "home-backend.jar"
    mergeServiceFiles()
}
