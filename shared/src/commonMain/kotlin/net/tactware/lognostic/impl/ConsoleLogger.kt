package net.tactware.lognostic.impl

import net.tactware.lognostic.LogLevel
import net.tactware.lognostic.Logger
import net.tactware.lognostic.currentTimeMillis

/**
 * A [Logger] implementation that writes all log messages to standard output.
 *
 * @param minLevel The minimum log level to output. Messages below this level are suppressed.
 */
class ConsoleLogger(
    private val minLevel: LogLevel = LogLevel.VERBOSE
) : Logger {

    override fun log(level: LogLevel, message: String, throwable: Throwable?) {
        if (level.priority < minLevel.priority) return
        val timestamp = formatTimestamp(currentTimeMillis())
        val levelLabel = level.name.padEnd(7)
        println("$timestamp [$levelLabel] $message")
        throwable?.printStackTrace()
    }

    private fun formatTimestamp(epochMillis: Long): String {
        val millis = (epochMillis % 1000).toInt().toString().padStart(3, '0')
        val totalSeconds = epochMillis / 1000
        val seconds = (totalSeconds % 60).toInt().toString().padStart(2, '0')
        val totalMinutes = totalSeconds / 60
        val minutes = (totalMinutes % 60).toInt().toString().padStart(2, '0')
        val totalHours = totalMinutes / 60
        val hours = (totalHours % 24).toInt().toString().padStart(2, '0')
        val daysSinceEpoch = totalHours / 24
        return "$daysSinceEpoch $hours:$minutes:$seconds.$millis"
    }
}
