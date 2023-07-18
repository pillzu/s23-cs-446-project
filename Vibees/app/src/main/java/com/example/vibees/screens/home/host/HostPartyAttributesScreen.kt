package com.example.vibees.screens.home.host

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HostPartyAttributesScreen(

) {
    val selectedlist = mutableListOf<String>()
    val listcount by remember { mutableIntStateOf(0) }
    val tags = listOf("Dance", "Board game", "Karaoke", "Barbecue", "Pool", "Disco", "Birthday",
        "Graduation", "Adult only", "Business", "Formal", "Wedding", "Sports", "Bar Hopping", "Day", "Early Night",
        "Late Night")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, top = 25.dp, end = 10.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Host a Party",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "The fun stuff!",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier
                    .padding(20.dp)
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Start
            )

            // tags section
            Text(
                text = "Select up to 5 tags that best describe your party:",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(12.dp),
                textAlign = TextAlign.Start
            )
            FlowRow(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(8.dp)
                    .padding(bottom = 8.dp)
            ) {
                for (element in tags) {
                    FilterChipTag(
                        name = element,
                        list = selectedlist
                    )
                }
            }

            // Image upload section
            Text(
                text = "Add an image to your party:",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Start
            )
            Box(
                Modifier
                    .size(250.dp, 80.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Gray,
                            style = Stroke(
                                width = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            ),
                            cornerRadius = CornerRadius(8.dp.toPx())
                        )
                    }
                    .clickable {
                        // image upload logic
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Icon",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Upload an image",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }


        TextButton(
            onClick = {  },
            modifier = Modifier.align(Alignment.End),
            enabled = listcount > 0
        ) {
            Text(
                "Create",
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = if (listcount > 0) Color.Blue else Color.Gray
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Arrow right"
            )
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipTag(
    name: String,
    list: MutableList<String>
) {
    var selected by remember { mutableStateOf(false) }
    FilterChip(
        selected = selected,
        onClick = {
                    selected = !selected
                    if (selected) list.add(name) else list.remove(name)
                  },
        label = { Text(name) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Localized Description",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
        modifier = Modifier.padding(4.dp)
    )
}