package net.tactware.lognostic.impl

import net.tactware.lognostic.AdvancedLogger
import net.tactware.lognostic.LogEntry
import net.tactware.lognostic.LogImportance
import net.tactware.lognostic.LogLevel
import net.tactware.lognostic.Logger
import net.tactware.lognostic.TagFilter
import net.tactware.lognostic.frequency.AllowAllFrequencyController
import net.tactware.lognostic.frequency.FrequencyController

/**
 * The primary implementation of [AdvancedLogger].
 *
 * Applies tag-based filtering, importance thresholds, and frequency control before
 * forwarding accepted log entries to one or more delegate [Logger] instances.
 *
 * @param delegates The loggers that receive accepted log entries.
 * @param tagFilters A list of [TagFilter] rules applied in order. The first matching
 *   filter determines whether the entry is accepted. If no filter matches, the entry
 *   is accepted using the global defaults.
 * @param globalMinLevel The minimum log level applied when no tag filter matches.
 * @param globalMinImportance The minimum importance applied when no tag filter matches.
 * @param frequencyController Controls whether a given message is allowed based on
 *   rate limiting, deduplication, or sampling rules.
 */
class AdvancedLoggerEngine(
    private val delegates: List<Logger>,
    private val tagFilters: List<TagFilter> = emptyList(),
    private val globalMinLevel: LogLevel = LogLevel.VERBOSE,
    private val globalMinImportance: LogImportance = LogImportance.LOW,
    private val frequencyController: FrequencyController = AllowAllFrequencyController()
) : AdvancedLogger {

    constructor(
        vararg delegates: Logger,
        tagFilters: List<TagFilter> = emptyList(),
        globalMinLevel: LogLevel = LogLevel.VERBOSE,
        globalMinImportance: LogImportance = LogImportance.LOW,
        frequencyController: FrequencyController = AllowAllFrequencyController()
    ) : this(delegates.toList(), tagFilters, globalMinLevel, globalMinImportance, frequencyController)

    override fun log(
        level: LogLevel,
        tag: String?,
        importance: LogImportance,
        message: String,
        throwable: Throwable?
    ) {
        if (!isAllowedByFilter(level, tag, importance)) return
        if (!frequencyController.shouldLog(tag, message)) return

        val entry = LogEntry(level, tag, importance, message, throwable)
        dispatch(entry)
    }

    private fun isAllowedByFilter(level: LogLevel, tag: String?, importance: LogImportance): Boolean {
        val matchingFilter = tagFilters.firstOrNull { it.matches(tag) }
        return matchingFilter?.allows(level, importance)
            ?: (level.priority >= globalMinLevel.priority && importance.weight >= globalMinImportance.weight)
    }

    private fun dispatch(entry: LogEntry) {
        val formattedMessage = buildMessage(entry)
        delegates.forEach { it.log(entry.level, formattedMessage, entry.throwable) }
    }

    private fun buildMessage(entry: LogEntry): String {
        return if (entry.tag != null) "[${entry.tag}][${entry.importance.name}] ${entry.message}"
        else "[${entry.importance.name}] ${entry.message}"
    }
}
