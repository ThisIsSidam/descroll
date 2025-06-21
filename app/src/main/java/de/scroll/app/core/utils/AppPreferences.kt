package app.wa.automate.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import de.scroll.app.core.constants.Platform
import de.scroll.app.core.constants.PlatformRestriction

object AppPreferences {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }


    // Platforms and Restrictions

    fun getPlatformRestriction(
        platform: Platform,
        defaultValue: PlatformRestriction = PlatformRestriction.NONE
    ): PlatformRestriction {
        val key = platform.id.toString()
        val value = prefs.getInt(key, defaultValue.id)
        return PlatformRestriction.fromId(value)
    }

    fun setPlatformRestriction(platform: Platform, restriction: PlatformRestriction) {
        val key = platform.id.toString()
        prefs.edit { putInt(key, restriction.id) }
    }

    fun getAllRestrictions() : Map<Platform, PlatformRestriction> {
        return Platform.entries.associate { platform ->
            platform to getPlatformRestriction(platform)
        }
    }


    // Duration Preferences for Pause Restriction

    /// Gets the duration in minutes for the pause restriction
    /// Default is 5 minutes
    fun getPauseDuration() : Int {
        return prefs.getInt("pause_duration", 1)
    }

    fun setPauseDuration(duration: Int) {
        prefs.edit { putInt("pause_duration", duration) }
    }

    /// Gets the interval for the pause cooldown in seconds
    /// You can pause the content again this much hours later.
    /// Default is 120 minutes (2 hours).
    fun getPauseCooldown() : Int {
        return prefs.getInt("pause_interval", 5) // Default to 60 minutes
    }

    fun setPauseCooldown(interval: Int) {
        prefs.edit { putInt("pause_interval", interval) }
    }

    /// Gets the next pause time in milliseconds
    fun getNextPauseTime(): Long? {
        return prefs.getLong("next_pause_time", -1L).takeIf { (it != -1L) }
    }

    fun setNextPauseTime(time: Long) {
        prefs.edit { putLong("next_pause_time", time) }
    }
}