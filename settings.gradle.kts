pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }

    val kotlinVersion = settings.extra["kotlin.version"].toString()
    val dokkaPluginVersion: String by settings
    val ktlintVersion: String by settings
    val nexusPluginVersion: String by settings
    val springBootVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        `maven-publish`
        id("org.jetbrains.dokka") version dokkaPluginVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
        id("io.github.gradle-nexus.publish-plugin") version nexusPluginVersion
        id("org.springframework.boot") version springBootVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    }
}

rootProject.name = "embedded-redis-spring"
