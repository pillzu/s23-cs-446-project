package com.example.vibees.screens.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cloudinary.android.MediaManager
import com.example.vibees.R
import com.example.vibees.ui.theme.Yellow

@Composable
fun HelpScreen() {
    Box(
        modifier = Modifier
                    .fillMaxSize(),
//        contentAlignment = Alignment.Center
    ) {
        CollapsableLazyColumn(
            sections = listOf(
                CollapsableSection(
                    title = stringResource(id = R.string.title_about_us),
                    rows = listOf(stringResource(id = R.string.description_about_us))
                ),
                CollapsableSection(
                    title = stringResource(id = R.string.title_contact_us),
                    rows = listOf(
                        stringResource(id = R.string.description_contact_email),
                        stringResource(id = R.string.description_contact_phone),
                        stringResource(id = R.string.description_contact_twitter),
                        stringResource(id = R.string.description_contact_discord),
                        stringResource(id = R.string.description_contact_facebook),
                        stringResource(id = R.string.description_contact_instagram)
                    )
                ),
                CollapsableSection(
                    title = stringResource(id = R.string.title_faq),
                    rows = listOf(
                        stringResource(id = R.string.description_faq_vibees_work),
                        stringResource(id = R.string.description_faq_dietary_restrictions),
                        stringResource(id = R.string.description_faq_cancel_attendance)
                    )
                ),
                CollapsableSection(
                    title = stringResource(id = R.string.title_privacy_policy),
                    rows = listOf(stringResource(id = R.string.description_privacy_policy))
                ),

                ),
        )
    }
}

@Composable
fun CollapsableLazyColumn(
    sections: List<CollapsableSection>,
    modifier: Modifier = Modifier
) {
    val collapsedState = remember(sections) { sections.map { true }.toMutableStateList() }
    LazyColumn(modifier ) {
        item(key="infographic") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Need a hand?",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp)
                )
                Text(text = "Find solutions to all your questions here",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Image(painter = painterResource(id = R.drawable.saly_10), contentDescription = "Help infographic")
                Spacer(modifier = Modifier.height(20.dp))
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        sections.forEachIndexed { i, dataItem ->
            val collapsed = collapsedState[i]
            item(key = "header_$i") {
                Box(
                    modifier = Modifier
                        .clickable {
                            collapsedState[i] = !collapsed
                        }
                        .padding(vertical = 10.dp)

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Icon(
                            Icons.Default.run {
                                if (collapsed)
                                    KeyboardArrowDown
                                else
                                    KeyboardArrowUp
                            },
                            contentDescription = "",
                            tint = Color.LightGray,
                            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                        )
                        Text(
                            dataItem.title,
                            style= MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Divider()
            }
            if (!collapsed) {
                items(dataItem.rows) { row ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 10.dp)

                    ) {
                        Spacer(modifier = Modifier.size(MaterialIconDimension.dp))
                        Text(
                            row,
                            modifier = Modifier
                                .padding(end = 16.dp)

                        )
                    }
                    Divider()
                }
            }
        }
        item() {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(text = "All rights Reserved @ Vibees; Version 1.0",
                    color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(10.dp)
                )
                Spacer(modifier = Modifier.height(60.dp))
            }

        }
    }
}


data class CollapsableSection(val title: String, val rows: List<String>)

const val MaterialIconDimension = 24f
