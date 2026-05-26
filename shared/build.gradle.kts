import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    `maven-publish`
}

group = "net.tactware.lognostic"
version = "1.0.0"

base {
    archivesName.set("LoggerWrapper")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    androidLibrary {
        namespace = "net.tactware.lognostic"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("Lognostic")
            description.set(
                "A comprehensive, flexible Kotlin Multiplatform logging library supporting routing, " +
                    "rate limiting, and deduplication."
            )
            url.set("https://github.com/TactWareInc/Lognostic")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("kmbisset89")
                    name.set("Kerry Bisset")
                    email.set("kerry.bisset@tactware.net")
                }
            }
            scm {
                connection.set("scm:git:git://github.github.com/TactWareInc/Lognostic.git")
                developerConnection.set("scm:git:ssh://github.github.com/TactWareInc/Lognostic.git")
                url.set("https://github.github.com/TactWareInc/Lognostic")
            }
        }
    }
}
