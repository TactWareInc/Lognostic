package net.tactware.lognostic.impl

import android.util.Log
import net.tactware.lognostic.LogLevel
import net.tactware.lognostic.Logger

/**
 * A [Logger] implementation that delegates to the Android [android.util.Log] API.
 *
 * @param defaultTag The tag used when no tag is supplied by the caller.
 */
class AndroidLogger(private val defaultTag: String = "App") : Logger {

    override fun log(level: LogLevel, message: String, throwable: Throwable?) {
        logWithTag(level, defaultTag, message, throwable)
    }

    /**
     * Logs a message with an explicit tag, routing to the appropriate [Log] method for the given level.
     */
    fun logWithTag(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.VERBOSE -> Log.v(tag, message, throwable)
            LogLevel.DEBUG -> Log.d(tag, message, throwable)
            LogLevel.INFO -> Log.i(tag, message, throwable)
            LogLevel.WARN -> Log.w(tag, message, throwable)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
            LogLevel.FATAL -> Log.wtf(tag, message, throwable)
        }
    }
}
