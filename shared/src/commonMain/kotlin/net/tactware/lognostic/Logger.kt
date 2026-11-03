package net.tactware.lognostic

/**
 * Represents the severity level of a log message.
 */
enum class LogLevel(val priority: Int) {
    V(2),
    VERBOSE(2),
    D(3),
    DEBUG(3),
    I(4),
    INFO(4),
    W(5),
    WARN(5),
    E(6),
    ERROR(6),
    F(7),
    FATAL(7)
}

/**
 * Represents the importance tier of a log message.
 * This is useful for advanced filtering, separate from standard log levels.
 */
enum class LogImportance(val weight: Int) {
    L(1),
    LOW(1),
    M(2),
    MEDIUM(2),
    H(3),
    HIGH(3),
    C(4),
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

    fun v(tag: String?=null, importance: LogImportance = LogImportance.MEDIUM, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.VERBOSE, tag=tag, importance = importance, message, throwable)
    fun v_l(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.VERBOSE, tag=tag, importance = LogImportance.LOW, message, throwable)
    fun v_m(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.VERBOSE, tag=tag, importance = LogImportance.MEDIUM, message, throwable)
    fun v_h(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.VERBOSE, tag=tag, importance = LogImportance.HIGH, message, throwable)
    fun v_c(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.VERBOSE, tag=tag, importance = LogImportance.CRITICAL, message, throwable)

    fun d(tag: String?=null, importance: LogImportance = LogImportance.MEDIUM, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.DEBUG, tag=tag, importance = importance, message, throwable)
    fun d_l(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.DEBUG, tag=tag, importance = LogImportance.LOW, message, throwable)
    fun d_m(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.DEBUG, tag=tag, importance = LogImportance.MEDIUM, message, throwable)
    fun d_h(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.DEBUG, tag=tag, importance = LogImportance.HIGH, message, throwable)
    fun d_c(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.DEBUG, tag=tag, importance = LogImportance.CRITICAL, message, throwable)

    fun i(tag: String?=null, importance: LogImportance = LogImportance.MEDIUM, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.INFO, tag=tag, importance = importance, message, throwable)
    fun i_l(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.INFO, tag=tag, importance = LogImportance.LOW, message, throwable)
    fun i_m(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.INFO, tag=tag, importance = LogImportance.MEDIUM, message, throwable)
    fun i_h(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.INFO, tag=tag, importance = LogImportance.HIGH, message, throwable)
    fun i_c(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.INFO, tag=tag, importance = LogImportance.CRITICAL, message, throwable)

    fun w(tag: String?=null, importance: LogImportance = LogImportance.MEDIUM, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.WARN, tag=tag, importance = importance, message, throwable)
    fun w_l(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.WARN, tag=tag, importance = LogImportance.LOW, message, throwable)
    fun w_m(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.WARN, tag=tag, importance = LogImportance.MEDIUM, message, throwable)
    fun w_h(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.WARN, tag=tag, importance = LogImportance.HIGH, message, throwable)
    fun w_c(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.WARN, tag=tag, importance = LogImportance.CRITICAL, message, throwable)


    fun e(tag: String?=null, importance: LogImportance = LogImportance.MEDIUM, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.ERROR, tag=tag, importance = importance, message, throwable)
    fun e_l(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.ERROR, tag=tag, importance = LogImportance.LOW, message, throwable)
    fun e_m(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.ERROR, tag=tag, importance = LogImportance.MEDIUM, message, throwable)
    fun e_h(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.ERROR, tag=tag, importance = LogImportance.HIGH, message, throwable)
    fun e_c(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.ERROR, tag=tag, importance = LogImportance.CRITICAL, message, throwable)

    fun wtf(tag: String?=null, importance: LogImportance = LogImportance.MEDIUM, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.FATAL, tag=tag, importance = importance, message, throwable)
    fun wtf_l(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.FATAL, tag=tag, importance = LogImportance.LOW, message, throwable)
    fun wtf_m(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.FATAL, tag=tag, importance = LogImportance.MEDIUM, message, throwable)
    fun wtf_h(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.FATAL, tag=tag, importance = LogImportance.HIGH, message, throwable)
    fun wtf_c(tag: String?=null, message: String, throwable: Throwable? = null) =
        log(level = LogLevel.FATAL, tag=tag, importance = LogImportance.CRITICAL, message, throwable)
}
