package com.example.vibees.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.outlined.Info
import androidx.compose.material3.icons.outlined.QuestionAnswer
import androidx.compose.material3.icons.outlined.Shield
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.widget.ExpandableListView

@Composable
fun HelpScreen(
    onClickAboutUs: () -> Unit,
    onClickFAQ: () -> Unit,
    onClickPrivacyPolicy: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ExpandableListView(
            items = listOf(
                ExpandableItem(
                    text = "About us",
                    icon = Icons.Outlined.Info,
                    onClick = onClickAboutUs,
                    content = "This is the about us section. It contains information about our company."
                ),
                ExpandableItem(
                    text = "FAQ",
                    icon = Icons.Outlined.QuestionAnswer,
                    onClick = onClickFAQ,
                    content = "Frequently Asked Questions:\n\n1. Question 1?\n   Answer 1\n\n2. Question 2?\n   Answer 2"
                ),
                ExpandableItem(
                    text = "Privacy Policy",
                    icon = Icons.Outlined.Shield,
                    onClick = onClickPrivacyPolicy,
                    content = "This is the privacy policy of our app."
                )
            ),
            header = { item ->
                TextItem(text = item.text, icon = item.icon)
            },
            expandedContent = { item ->
                Card(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                    Text(
                        text = item.content,
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        )
    }
}

@Composable
private fun TextItem(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

data class ExpandableItem(
    val text: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val content: String
)
