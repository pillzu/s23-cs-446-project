package com.example.vibees.screens.home.host

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.naingaungluu.formconductor.FormResult

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
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = Color.Black
        )

        Text(
            "Agree to the guidelines and be set to host!",
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = Color.Gray
        )

        Text(
            "For the safety and well-being of the community around you, please ensure that " +
                    "you keep noise pollution to a minimum, especially during the hours of 10pm to" +
                    "7am. If there are substances or harmful supplies which may be used in the party," +
                    " inform your neighbours and attendees prior to your party. Prioritize their as well as your welfare and " +
                    "enjoy your party safely!",
            modifier = Modifier.padding(20.dp),
            fontWeight = FontWeight.Normal,
            fontSize = MaterialTheme.typography.titleSmall.fontSize,
            color = Color.Black
        )

        Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)) {
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