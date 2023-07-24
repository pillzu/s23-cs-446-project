package com.example.vibees.screens.home.host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HostAgreementScreen(onClick: () -> Unit) {
    var checked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Want to host a party?",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = Color.Black
        )

        Text(
            "Accept the guidelines and get ready to rock!",
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = Color.Gray
        )

        Text(
            "As a host, please agree to the following guidelines to ensure the safety and well-being of " +
                    "the community during your parties and events:",
            modifier = Modifier.padding(5.dp, 20.dp, 5.dp, 15.dp),
            fontWeight = FontWeight.Normal,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            color = Color.Black
        )
        val bullet = "\u2022"
        val messages = listOf(
            "Control noise levels during quiet hours between 10pm and 7am to be considerate of neighbors",
            "Obtain necessary permits for large-scale events and adhere to safety measures for the welfare of attendees",
            "Disclose any potentially harmful party supplies to guests and nearby residents, using them responsibly and disposing of them properly",
            "Implement strict safety protocols if your event involves hazardous materials or pyrotechnics to prevent accidents",
            "Ensure compliance with age restrictions for alcohol consumption to maintain a safe environment and prevent underage drinking"
        )
        val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
        Text(
            buildAnnotatedString {
                messages.forEach {
                    withStyle(style = paragraphStyle) {
                        append(bullet)
                        append("\t\t")
                        append(it)
                    }
                }
            },
            modifier = Modifier.padding(5.dp, 0.dp, 5.dp, 10.dp),
            fontWeight = FontWeight.Light,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            color = Color.Black
        )

        Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)) {
            Checkbox(checked = checked,
                onCheckedChange = { checked_ -> checked = checked_ },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Gray,
                    uncheckedColor = Color.Black))

            Text(
                "I understand and agree to the above guidelines",
                modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 15.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = Color.Black
            )
        }

        Button(onClick = onClick,
               enabled = checked,
               colors = ButtonDefaults.buttonColors(
                   disabledContentColor = Color.Gray
               )) {
            Text(
                "Host Party",
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = Color.Black
            )
        }
    }
}