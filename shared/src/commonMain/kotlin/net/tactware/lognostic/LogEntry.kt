package net.tactware.lognostic


/**
 * Represents a single log event with all associated metadata.
 *
 * @property level The severity level of the log.
 * @property tag An optional tag to categorize or identify the source of the log.
 * @property importance The importance tier of the log.
 * @property message The log message.
 * @property throwable An optional exception associated with the log.
 * @property timestamp The time at which the log was created, in milliseconds since epoch.
 */
data class LogEntry(
    val level: LogLevel,
    val tag: String?,
    val importance: LogImportance,
    val message: String,
    val throwable: Throwable? = null,
    val timestamp: Long = currentTimeMillis()
)
