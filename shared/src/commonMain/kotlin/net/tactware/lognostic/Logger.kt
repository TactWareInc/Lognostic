package net.tactware.lognostic

/**
 * Represents the severity level of a log message.
 */
enum class LogLevel(val priority: Int) {
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6),
    FATAL(7)
}

/**
 * Represents the importance tier of a log message.
 * This is useful for advanced filtering, separate from standard log levels.
 */
enum class LogImportance(val weight: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4)
}

/**
 * The basic Logger interface.
 * Designed to be ingested by libraries without pulling in heavy dependencies.
 */
interface Logger {
    /**
     * Logs a message with the specified level.
     */
    fun log(level: LogLevel, message: String, throwable: Throwable? = null)

    fun v(message: String, throwable: Throwable? = null) = log(LogLevel.VERBOSE, message, throwable)
    fun d(message: String, throwable: Throwable? = null) = log(LogLevel.DEBUG, message, throwable)
    fun i(message: String, throwable: Throwable? = null) = log(LogLevel.INFO, message, throwable)
    fun w(message: String, throwable: Throwable? = null) = log(LogLevel.WARN, message, throwable)
    fun e(message: String, throwable: Throwable? = null) = log(LogLevel.ERROR, message, throwable)
    fun wtf(message: String, throwable: Throwable? = null) = log(LogLevel.FATAL, message, throwable)
}

/**
 * Advanced Logger interface extending the basic Logger.
 * Adds support for tags, importance filtering, and frequency control.
 */
interface AdvancedLogger : Logger {
    /**
     * Logs a message with advanced parameters.
     *
     * @param level The severity level of the log.
     * @param tag An optional tag to categorize the log.
     * @param importance The importance tier of the log.
     * @param message The log message.
     * @param throwable An optional exception associated with the log.
     */
    fun log(
        level: LogLevel,
        tag: String? = null,
        importance: LogImportance = LogImportance.MEDIUM,
        message: String,
        throwable: Throwable? = null
    )

    // Override the basic log to route to the advanced log with default values
    override fun log(level: LogLevel, message: String, throwable: Throwable?) {
        log(level, null, LogImportance.MEDIUM, message, throwable)
    }
}
