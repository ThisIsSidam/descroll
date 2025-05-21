package de.scroll.app.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.scroll.app.core.constants.PlatformRestriction
import de.scroll.app.R
import de.scroll.app.core.constants.Platform

@Composable
fun PlatformCard(
    platform: Platform,
    isExpanded: Boolean = false,
    onTapOutside: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable {
                onTapOutside()
            }
            .animateContentSize()
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = platform.iconRes),
                    contentDescription = platform.label,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = platform.label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.weight(1f))
                if (!isExpanded) {
                    Icon(
                        painter = painterResource(R.drawable.block),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.size(12.dp))
                RestrictionPanel()
                Spacer(modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun RestrictionPanel() {

    var selected by remember { mutableStateOf<PlatformRestriction>(PlatformRestriction.NONE) }
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 34.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlatformRestriction.entries.forEach {  entry ->
            val isSelected = entry == selected

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) colorScheme.background.copy(alpha = 0.5f)
                        else colorScheme.onBackground.copy(alpha = 0.1f)
                    )
                    .clickable { selected = entry },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(entry.iconCode),
                    contentDescription = "Platform restriction icon to change restriction: ${entry.title}",
                    tint = colorScheme.onBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

}