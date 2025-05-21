package de.scroll.app.ui.components

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.scroll.app.R
import androidx.core.content.edit
import de.scroll.app.core.constants.Platform

@Composable
fun PlatformsSection(preferences: SharedPreferences) {
    val ytAllowed : Boolean = preferences.getBoolean("yt_allowed", true)
    val instaAllowed : Boolean = preferences.getBoolean("insta_allowed", true)
    var ytEnabled by remember { mutableStateOf(ytAllowed) }
    var instaEnabled by remember { mutableStateOf(instaAllowed) }

    var expanded by remember { mutableStateOf<Platform?>(null) }

    Column(modifier = Modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        Platform.entries.forEach { entry ->
            PlatformCard(
                platform = entry,
                isExpanded = expanded == entry,
            ) {
                if (expanded == entry) {
                    expanded = null
                } else {
                    expanded = entry
                }
            }
        }


    }
}
