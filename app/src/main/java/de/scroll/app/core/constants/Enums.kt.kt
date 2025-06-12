package de.scroll.app.core.constants

import de.scroll.app.R

enum class PlatformRestriction(val id: Int, val title: String, val iconCode: Int, val description: String) {
    NONE(
        101,
        "No restriction",
        R.drawable.filter_tilt_shift,
        "No restrictions"
    ),
    BLOCK(
        202,
        "Blocked",
        R.drawable.block,
        "Feature is blocked, moved to home if clicked"
    ),
    PAUSE(
        303,
        "Pause",
        R.drawable.outline_autopause_24,
        "Lets you watch the content for a short amount of time every couple hours. You can set both timers."
    )
    ;

    companion object  {
        fun fromId(id: Int) : PlatformRestriction {
            for (restriction in entries) {
                if (restriction.id == id) {
                    return restriction
                }
            }
            return NONE
        }
    }
}

enum class Platform(val id: Int, val label: String, val iconRes: Int) {
    YOUTUBE_SHORTS(
        1,
        "Youtube Shorts",
        R.drawable.ic_youtube
    ),
    INSTAGRAM_REELS(
        2,
        "Instagram Reels",
        R.drawable.ic_instagram
    )
}