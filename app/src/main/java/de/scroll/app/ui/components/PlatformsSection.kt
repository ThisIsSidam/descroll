package de.scroll.app.ui.components

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.scroll.app.R
import androidx.core.content.edit

@Composable
fun PlatformsSection(preferences: SharedPreferences) {
    val ytAllowed : Boolean = preferences.getBoolean("yt_allowed", true)
    val instaAllowed : Boolean = preferences.getBoolean("insta_allowed", true)
    var ytEnabled by remember { mutableStateOf(ytAllowed) }
    var instaEnabled by remember { mutableStateOf(instaAllowed) }

    Column(modifier = Modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        PlatformCard(
            iconRes = R.drawable.ic_youtube,
            label = "Youtube Shorts",
            isChecked = ytEnabled,
            onCheckedChange = {
                ytEnabled = it
                preferences.edit { putBoolean("yt_allowed", ytEnabled) }
            }
        )

        PlatformCard(
            iconRes = R.drawable.ic_instagram,
            label = "Instagram Reels",
            isChecked = instaEnabled,
            onCheckedChange = {
                instaEnabled = it
                preferences.edit { putBoolean("insta_allowed", instaEnabled) }
            }
        )
    }
}
