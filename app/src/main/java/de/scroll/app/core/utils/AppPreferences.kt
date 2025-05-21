package app.wa.automate.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import de.scroll.app.core.constants.PlatformRestriction

object AppPreferences {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    fun getPlatformRestriction(
        key: String,
        defaultValue: PlatformRestriction = PlatformRestriction.NONE
    ): PlatformRestriction {
        val value = prefs.getInt(key, defaultValue.id)
        return PlatformRestriction.fromId(value)
    }

    fun setPlatformRestriction(key: String, value: PlatformRestriction) {
        prefs.edit { putInt(key, value.id) }
    }
}