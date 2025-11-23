plugins {
    id("java")
}

group = "com.anonymouslyfast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("net.minestom:minestom:2025.10.18-1.21.10")
    implementation("org.xerial:sqlite-jdbc:3.45.3.0")
    // For testing purposes on mac until I can get viaproxy on :>
    // implementation("net.minestom:minestom:2025.10.05-1.21.8")
}

tasks.test {
    useJUnitPlatform()
}