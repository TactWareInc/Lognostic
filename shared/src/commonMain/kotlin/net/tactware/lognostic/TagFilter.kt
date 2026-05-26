package net.tactware.lognostic

/**
 * Defines a filter rule for routing or suppressing logs based on tag patterns.
 *
 * @property pattern A string pattern to match against log tags. Supports exact match and wildcard (*).
 * @property minLevel The minimum log level required for the tag to be logged.
 * @property minImportance The minimum importance required for the tag to be logged.
 */
data class TagFilter(
    val pattern: String,
    val minLevel: LogLevel = LogLevel.VERBOSE,
    val minImportance: LogImportance = LogImportance.LOW
) {
    /**
     * Returns true if the given tag matches this filter's pattern.
     */
    fun matches(tag: String?): Boolean {
        if (tag == null) return pattern == "*"
        return when {
            pattern == "*" -> true
            pattern.endsWith("*") -> tag.startsWith(pattern.dropLast(1))
            pattern.startsWith("*") -> tag.endsWith(pattern.drop(1))
            else -> tag == pattern
        }
    }

    /**
     * Returns true if the given level and importance satisfy this filter's thresholds.
     */
    fun allows(level: LogLevel, importance: LogImportance): Boolean {
        return level.priority >= minLevel.priority && importance.weight >= minImportance.weight
    }
}
