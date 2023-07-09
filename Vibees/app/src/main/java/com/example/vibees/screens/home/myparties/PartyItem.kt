package com.example.vibees.screens.home.myparties

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.R
import com.example.vibees.ui.theme.SubtleWhite
import com.example.vibees.ui.theme.Yellow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun PartyCardAttribute(
    modifier: Modifier = Modifier,
    key: String?,
    value: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
    ) {

        Text(
            text = "$key: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        if (value != null) {
            Text(
                text = value,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun PartyItem(
    partyInfo: Party,
    isMyParty: Boolean,
    onClick: (id: String) -> Unit
) {
    var partyDetails by GlobalAppState::PartyDetails
    Card(
        colors = CardDefaults.elevatedCardColors(
            containerColor = SubtleWhite,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = CardDefaults.elevatedShape,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                partyDetails = partyInfo
                onClick(partyInfo.party_id!!)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = partyInfo.name!!,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                Text(
                    text = "${partyInfo.date_time!!.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 15.dp),
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 15.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.6f)
                ) {
                    PartyCardAttribute(
                        key = "Time", value = partyInfo.date_time.toLocalTime().format(
                            DateTimeFormatter.ofPattern("hh:mm a")
                        )
                    )
                    PartyCardAttribute(key = "Entry Fee", value = "$${partyInfo.entry_fee}")
                    PartyCardAttribute(key = "Host", value = partyInfo.host_name)
                    PartyCardAttribute(
                        key = "Location",
                        value = "${partyInfo.street}, ${partyInfo.city}"
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "${partyInfo.name} Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}

//Column (
//verticalArrangement = Arrangement.SpaceBetween,
//horizontalAlignment = Alignment.CenterHorizontally,
//modifier = Modifier
//.shadow(elevation = 1.dp, shape = RoundedCornerShape(4.dp))
//.padding(15.dp)
//.clip(RoundedCornerShape(10.dp))
//.fillMaxWidth()
//.clickable {
//    partyDetails = partyInfo
//    onClick(partyInfo.party_id!!) }
//) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 10.dp, end = 10.dp)
//        ) {
//            Column {
//                Text(
//                    text = partyInfo.name!!,
//                    style = MaterialTheme.typography.headlineLarge,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(bottom=10.dp)
//                )
//                Row {
//                    Text(
//                        text = "Date-Time: ",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = partyInfo.date_time.toString(),
//                        style = MaterialTheme.typography.bodyLarge,
//                    )
//                }
//                if (!isMyParty) {
//                Row {
//                        Text(
//                            text = "Entry Fee: ",
//                            style = MaterialTheme.typography.bodyLarge,
//                            fontWeight = FontWeight.Bold
//                        )
//                        Text(
//                            text = partyInfo.entry_fee.toString(),
//                            style = MaterialTheme.typography.bodyLarge,
//                        )
//                    }
//                }
//                Row {
//                    Text(
//                        text = "Host: ",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = partyInfo.host_name!!,
//                        style = MaterialTheme.typography.bodyLarge,
//                    )
//                }
//                Row {
//                    Text(
//                        text = "Location: ",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = partyInfo.street,
//                        style = MaterialTheme.typography.bodyLarge,
//                    )
//                }
//            }
//        }
//        Row(
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp)
//        ) {
//            if (isMyParty) {
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(5.dp))
//                        .background(MaterialTheme.colorScheme.primary)
//                        .padding(horizontal = 10.dp, vertical = 10.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Share,
//                        contentDescription = "QR Code icon",
//                        modifier = Modifier.size(20.dp).padding(horizontal = 2.dp)
//                    )
//                    Text(
//                        text = "SCAN QR",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold,
//                    )
//                }
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(5.dp))
//                        .background(Color.Red)
//                        .padding(horizontal = 10.dp, vertical = 10.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Cancel icon",
//                        tint = MaterialTheme.colorScheme.tertiary,
//                        modifier = Modifier.size(20.dp).padding(horizontal = 2.dp)
//                    )
//                    Text(
//                        text = "CANCEL",
//                        style = MaterialTheme.typography.bodyLarge,
//                        fontWeight = FontWeight.Bold,
//                        color = MaterialTheme.colorScheme.tertiary,
//
//                    )
//                }
//            }
//        }
//    }
//}