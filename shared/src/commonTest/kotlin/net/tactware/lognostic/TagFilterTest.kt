package net.tactware.lognostic

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TagFilterTest {

    @Test
    fun exactPatternMatchesOnlyTheExactTag() {
        val filter = TagFilter("network")
        assertTrue(filter.matches("network"))
        assertFalse(filter.matches("network2"))
        assertFalse(filter.matches("net"))
        assertFalse(filter.matches(null))
    }

    @Test
    fun wildcardPatternMatchesAnyTag() {
        val filter = TagFilter("*")
        assertTrue(filter.matches("anything"))
        assertTrue(filter.matches("network"))
        assertTrue(filter.matches(null))
    }

    @Test
    fun prefixWildcardMatchesTagsEndingWithSuffix() {
        val filter = TagFilter("*Logger")
        assertTrue(filter.matches("NetworkLogger"))
        assertTrue(filter.matches("Logger"))
        assertFalse(filter.matches("LoggerFactory"))
    }

    @Test
    fun suffixWildcardMatchesTagsStartingWithPrefix() {
        val filter = TagFilter("network*")
        assertTrue(filter.matches("network"))
        assertTrue(filter.matches("networkHttp"))
        assertFalse(filter.matches("mynetwork"))
    }

    @Test
    fun allowsReturnsTrueWhenLevelAndImportanceMeetThresholds() {
        val filter = TagFilter("*", minLevel = LogLevel.WARN, minImportance = LogImportance.HIGH)
        assertTrue(filter.allows(LogLevel.WARN, LogImportance.HIGH))
        assertTrue(filter.allows(LogLevel.ERROR, LogImportance.CRITICAL))
        assertFalse(filter.allows(LogLevel.INFO, LogImportance.HIGH))
        assertFalse(filter.allows(LogLevel.WARN, LogImportance.MEDIUM))
    }
}
