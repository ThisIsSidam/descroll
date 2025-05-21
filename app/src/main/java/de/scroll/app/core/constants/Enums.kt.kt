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
    PARTIAL_BLOCK(
        303,
        "Partially blocked",
        R.drawable.repeat_one,
        "Lets you see a single shorts, stops you from scrolling"
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