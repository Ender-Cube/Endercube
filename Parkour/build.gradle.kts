group = "net.endercube"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
}

tasks.test {
    useJUnitPlatform()
}