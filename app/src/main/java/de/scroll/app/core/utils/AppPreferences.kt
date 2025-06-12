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
}