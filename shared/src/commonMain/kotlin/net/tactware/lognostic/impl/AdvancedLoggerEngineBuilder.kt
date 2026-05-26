package net.tactware.lognostic.impl

import net.tactware.lognostic.LogImportance
import net.tactware.lognostic.LogLevel
import net.tactware.lognostic.Logger
import net.tactware.lognostic.TagFilter
import net.tactware.lognostic.frequency.AllowAllFrequencyController
import net.tactware.lognostic.frequency.FrequencyController

/**
 * A fluent builder for constructing an [AdvancedLoggerEngine].
 *
 * Example usage:
 * ```kotlin
 * val logger = AdvancedLoggerEngineBuilder()
 *     .addDelegate(ConsoleLogger())
 *     .setGlobalMinLevel(LogLevel.DEBUG)
 *     .setGlobalMinImportance(LogImportance.MEDIUM)
 *     .addTagFilter(TagFilter("network*", minLevel = LogLevel.WARN))
 *     .setFrequencyController(RateLimitingFrequencyController(maxLogsPerWindow = 5))
 *     .build()
 * ```
 */
class AdvancedLoggerEngineBuilder {

    private val delegates = mutableListOf<Logger>()
    private val tagFilters = mutableListOf<TagFilter>()
    private var globalMinLevel: LogLevel = LogLevel.VERBOSE
    private var globalMinImportance: LogImportance = LogImportance.LOW
    private var frequencyController: FrequencyController = AllowAllFrequencyController()

    fun addDelegate(logger: Logger): AdvancedLoggerEngineBuilder = apply {
        delegates.add(logger)
    }

    fun addTagFilter(filter: TagFilter): AdvancedLoggerEngineBuilder = apply {
        tagFilters.add(filter)
    }

    fun setGlobalMinLevel(level: LogLevel): AdvancedLoggerEngineBuilder = apply {
        globalMinLevel = level
    }

    fun setGlobalMinImportance(importance: LogImportance): AdvancedLoggerEngineBuilder = apply {
        globalMinImportance = importance
    }

    fun setFrequencyController(controller: FrequencyController): AdvancedLoggerEngineBuilder = apply {
        frequencyController = controller
    }

    /**
     * Builds and returns the configured [AdvancedLoggerEngine].
     * @throws IllegalStateException if no delegate loggers have been added.
     */
    fun build(): AdvancedLoggerEngine {
        check(delegates.isNotEmpty()) { "At least one delegate logger must be added before building." }
        return AdvancedLoggerEngine(
            delegates = delegates.toList(),
            tagFilters = tagFilters.toList(),
            globalMinLevel = globalMinLevel,
            globalMinImportance = globalMinImportance,
            frequencyController = frequencyController
        )
    }
}
