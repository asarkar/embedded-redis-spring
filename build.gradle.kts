import java.util.Base64

plugins {
    kotlin("jvm")
    id("org.springframework.boot") apply false
    `maven-publish`
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.github.gradle-nexus.publish-plugin")
    id("org.jetbrains.kotlin.plugin.spring")
    signing
}

val projectGroup: String by project
val projectVersion: String by project
val projectDescription: String by project
group = projectGroup
version = projectVersion
description = projectDescription

repositories {
    mavenCentral()
    jcenter()
}

apply(plugin = "io.spring.dependency-management")

val jUnitVersion: String by project
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.junit:junit-bom:$jUnitVersion")
    }
}

val embeddedRedisVersion: String by project
val lettuceVersion: String by project
val springBootVersion: String by project
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion") {
        exclude(group = "com.vaadin.external.google")
    }
    implementation("org.signal:embedded-redis:$embeddedRedisVersion") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    testImplementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.lettuce:lettuce-core:$lettuceVersion")
}

plugins.withType<JavaPlugin> {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
    dokkaSourceSets.configureEach {
        jdkVersion.set(8)
        skipEmptyPackages.set(true)
        platform.set(org.jetbrains.dokka.Platform.jvm)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Creates a sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val kdocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Creates KDoc"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

tasks.jar.configure {
    finalizedBy(sourcesJar, kdocJar)
}

val licenseName: String by project
val licenseUrl: String by project
val developerName: String by project
val developerEmail: String by project
val gitHubUsername: String by project

val gitHubUrl: String by lazy { "github.com/$gitHubUsername/${project.name}" }

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(kdocJar)
            artifact(sourcesJar)
            pom {
                name.set("${project.group}:${project.name}")
                description.set(project.description)
                url.set("https://$gitHubUrl")
                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }
                developers {
                    developer {
                        name.set(developerName)
                        email.set(developerEmail)
                    }
                }
                scm {
                    connection.set("scm:git:git://$gitHubUrl.git")
                    developerConnection.set("scm:git:ssh://github.com:$gitHubUsername/${project.name}.git")
                    url.set("https://$gitHubUrl")
                }
            }
        }
    }
}

fun base64Decode(prop: String): String? {
    return project.findProperty(prop)?.let {
        String(Base64.getDecoder().decode(it.toString())).trim()
    }
}

signing {
    useInMemoryPgpKeys(base64Decode("signingKey"), base64Decode("signingPassword"))
    sign(*publishing.publications.toTypedArray())
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(base64Decode("sonatypeUsername"))
            password.set(base64Decode("sonatypePassword"))
        }
    }
}
