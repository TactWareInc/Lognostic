package net.tactware.lognostic.frequency

/**
 * Interface for controlling the frequency of log messages.
 */
interface FrequencyController {
    /**
     * Determines whether a log message should be allowed based on frequency rules.
     *
     * @param tag The tag of the log message.
     * @param message The log message itself.
     * @return true if the log should be allowed, false if it should be suppressed.
     */
    fun shouldLog(tag: String?, message: String): Boolean
}

/**
 * A basic frequency controller that allows all logs.
 */
class AllowAllFrequencyController : FrequencyController {
    override fun shouldLog(tag: String?, message: String): Boolean = true
}
