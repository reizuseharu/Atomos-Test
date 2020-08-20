import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

val projectGroup = "com.reizu.core"
val projectArtifact = "atomos-test"
val projectVersion = "0.0.1-pre-alpha"

group = projectGroup
version = projectVersion

apply(from = "gradle/constants.gradle.kts")

plugins {
    java
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.noarg") version "1.3.72"
    kotlin("plugin.allopen") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("org.jetbrains.dokka") version "0.9.17"
    idea
    `maven-publish`
}

repositories {
    mavenCentral()
    jcenter()
}


apply(from = "gradle/dependencies.gradle.kts")

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    withType<Wrapper> {
        gradleVersion = "5.0"
    }

    withType<DokkaTask> {
        outputFormat = "html"
        outputDirectory = "$buildDir/docs/dokka"
    }
}

sourceSets.create("integrationTest") {
    java.srcDir(file("src/integrationTest/java"))
    java.srcDir(file("src/integrationTest/kotlin"))
    resources.srcDir(file("src/integrationTest/resources"))
    compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
    runtimeClasspath += output + compileClasspath
}

val test: Test by tasks
val integrationTest by tasks.creating(Test::class) {
    description = "Runs the integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    // dependsOn(testDatabaseBuild)
    mustRunAfter(test)
}


val check by tasks.getting {
    dependsOn(integrationTest)
}
val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val repoUsername: String by project
val repoToken: String by project

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/reizuseharu/Atomos-Test")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: repoUsername
                password = project.findProperty("gpr.key") as String? ?: repoToken
            }
        }
    }
    publications {
        register("gpr", MavenPublication::class) {
            groupId = projectGroup
            artifactId = projectArtifact
            version = projectVersion
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}
