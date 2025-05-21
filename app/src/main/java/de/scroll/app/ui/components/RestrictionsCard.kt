package de.scroll.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import de.scroll.app.core.constants.PlatformRestriction

@Preview
@Composable
fun RestrictionsCard() {
    var selectedRestriction by remember { mutableStateOf<PlatformRestriction?>(null) }

    Card(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PlatformRestriction.entries.forEach { entry ->
                    RestrictionElement(entry, selectedRestriction == entry) {
                        selectedRestriction = if (selectedRestriction == entry) {
                            null
                        } else {
                            entry
                        }
                    }
                }
            }


            if (selectedRestriction != null) {

                Text(
                    text = "- ${selectedRestriction!!.title}",
                    modifier = Modifier
                        .padding(top = 4.dp, end = 16.dp, start = 24.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = selectedRestriction!!.description,
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 16.dp, end = 16.dp, start = 28.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun RestrictionElement(restriction: PlatformRestriction, isSelected : Boolean,   onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(restriction.iconCode),
                contentDescription = restriction.title,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
        }
    }

}