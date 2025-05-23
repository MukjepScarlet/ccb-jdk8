plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "moe.lasoleil.toolkit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val okhttpVersion = "5.0.0-alpha.14"
val jacksonVersion = "2.18.3"

val localLibs: Configuration by configurations.creating

dependencies {
    // HTTP
    api("com.squareup.okhttp3:okhttp:$okhttpVersion")

    // Jackson XML & JSON
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // BouncyCastle
    api("org.bouncycastle:bcprov-jdk18on:1.80")

    val localJar = fileTree("libs") { include("*.jar") }

    localLibs(localJar)
    compileOnly(localJar)

    testImplementation(kotlin("test"))
}

tasks.withType<Jar> {
    from({
        localLibs.resolve().map { zipTree(it) }
    })
    archiveBaseName = project.name
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = version
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            // Debug
//            pom.withXml {
//                asNode().get("dependencies")?.let {
//                    println("POM dependencies: $it")
//                }
//            }
        }
    }
    repositories {
        mavenLocal()
    }
}
