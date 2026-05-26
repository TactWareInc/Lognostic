# LoggerWrapper

A comprehensive, flexible Kotlin Multiplatform logging library for JVM and Android.

LoggerWrapper is designed with two primary use cases in mind:
1. **Library Authors:** Ingest the lightweight `Logger` interface so consumers can inject their own logging mechanism without pulling in heavy transitive dependencies.
2. **Application Developers:** Use the `AdvancedLogger` and `AdvancedLoggerEngine` to gain fine-grained control over log routing, frequency limiting (rate limiting, deduplication, sampling), and tag/importance-based filtering.

## Project structure

This project follows the [KMP Wizard 2026](https://blog.jetbrains.com/kotlin/2026/05/new-kmp-default-structure/) layout:

- **`shared`** — Kotlin Multiplatform library (`commonMain` + `jvm` + `android`)
- Platform-specific code (e.g. `AndroidLogger`) lives in `shared/src/androidMain`

Built with Kotlin **2.3.21**, AGP **9.0.1**, and the `com.android.kotlin.multiplatform.library` plugin.

## Features

- **Basic Logger Interface:** Lightweight and simple (`v`, `d`, `i`, `w`, `e`, `wtf`).
- **Advanced Logger Engine:**
  - Tag-based routing and filtering (supports wildcards).
  - Importance tiers (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`) independent of log severity.
  - **Frequency Control:**
    - `RateLimitingFrequencyController`: Limit max logs per tag in a time window.
    - `DeduplicatingFrequencyController`: Suppress identical repeated messages.
    - `SamplingFrequencyController`: Log only 1 in every N occurrences.
- **Built-in Implementations:**
  - `ConsoleLogger`: Standard output (common code).
  - `NoOpLogger`: Silent logger for tests or defaults.
  - `CompositeLogger`: Fan-out to multiple loggers.
  - `AndroidLogger`: Direct `android.util.Log` wrapper (`androidMain`).

## Installation

```kotlin
dependencies {
    implementation("com.example:LoggerWrapper:1.0.0")
}
```

Gradle resolves the correct JVM or Android variant from the published KMP metadata.

For local development:

```kotlin
dependencies {
    implementation(projects.shared)
}
```

## Usage

### 1. Basic Usage (For Libraries)

Expose the `Logger` interface in your library:

```kotlin
class MyLibrary(private val logger: Logger = NoOpLogger()) {
    fun doWork() {
        logger.i("Work started")
        // ...
    }
}
```

### 2. Advanced Usage (For Applications)

Build an `AdvancedLoggerEngine` to control high-volume logging:

```kotlin
val engine = AdvancedLoggerEngineBuilder()
    .addDelegate(ConsoleLogger())
    .setGlobalMinLevel(LogLevel.INFO)
    .setGlobalMinImportance(LogImportance.MEDIUM)
    .addTagFilter(TagFilter("Network*", minLevel = LogLevel.VERBOSE, minImportance = LogImportance.LOW))
    .setFrequencyController(
        CompositeFrequencyController(
            DeduplicatingFrequencyController(windowMillis = 5000),
            RateLimitingFrequencyController(maxLogsPerWindow = 10, windowMillis = 1000)
        )
    )
    .build()

engine.log(LogLevel.ERROR, tag = "NetworkHTTP", importance = LogImportance.HIGH, message = "Connection timeout")
```

### Android

```kotlin
val logger = AndroidLogger(defaultTag = "MyApp")
logger.i("Ready")
```

## Building

JVM (no Android SDK required):

```bash
./gradlew :shared:jvmTest
./gradlew :shared:publishJvmPublicationToMavenLocal
```

Full build including Android requires the Android SDK (`ANDROID_HOME` or `local.properties` with `sdk.dir`):

```bash
./gradlew :shared:check
./gradlew :shared:publishToMavenLocal
```

## License

This project is licensed under the Apache License 2.0.
