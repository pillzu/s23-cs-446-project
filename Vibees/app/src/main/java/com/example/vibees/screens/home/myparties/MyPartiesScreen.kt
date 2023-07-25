package com.example.vibees.screens.home.myparties

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vibees.Api.APIInterface
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.screens.user.Header
import com.example.vibees.ui.theme.SubtleWhite


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPartiesScreen(
    onClick: (id: String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 25.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {

        var userID by GlobalAppState::UserID
        var attendingParties by remember { mutableStateOf(listOf<Party>()) }
        var hostingParties by remember { mutableStateOf(listOf<Party>()) }
        // fetch all parties from endpoint /parties
        val apiService = APIInterface()

        val vibeesApi = VibeesApi()

        // Successful request
        val successfn_attending: (List<Party>) -> Unit = { response ->
            Log.d("TAG", "success")
            attendingParties = response
            Log.d("TAG attending", attendingParties.toString())
        }

        // failed request
        val failurefn_hosting: (Throwable) -> Unit = { t ->
            Log.d("TAG", "FAILURE: hosting")
            Log.d("TAG failure", t.printStackTrace().toString())
        }

        val failurefn_attending: (Throwable) -> Unit = { t ->
            Log.d("TAG", "FAILURE: attending")
            Log.d("TAG", t.printStackTrace().toString())
        }

        // Successful request
        val successfn_hosting: (List<Party>) -> Unit = { response ->
            Log.d("TAG", "success")
            hostingParties = response
            Log.d("TAG hosting", hostingParties.toString())
        }

        val responseHosting = vibeesApi.getPartiesHosting(successfn_hosting, failurefn_hosting)

        val responseAttending =
            vibeesApi.getPartiesAttending(successfn_attending, failurefn_attending)

        // Header
        Header(firstLine = "MY", secondLine = "PARTIES")
        Divider(modifier = Modifier.padding(vertical=10.dp),thickness = 1.dp, color = MaterialTheme.colorScheme.tertiary)
            // parties
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
            ) {
                item {
                    Text(
                        text = "Hosted by Me",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = 5.dp, top = 10.dp)
                    )
                }

                if (hostingParties.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .height(75.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "You are currently not hosting any parties",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(bottom = 5.dp, top = 10.dp)
                            )
//                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(hostingParties.size) {
                        PartyItem(partyInfo = hostingParties[it], isMyParty = true, onClick = onClick)
                    }
                }

                item {
                    Text(
                        text = "Upcoming Parties",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier
                            .padding(bottom = 5.dp, top = 10.dp)
                    )
                }
                if (attendingParties.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .height(75.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "You are currently not attending any parties",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(bottom = 5.dp, top = 10.dp)
                            )
//                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(attendingParties.size) {
                        PartyItem(
                            partyInfo = attendingParties[it],
                            isMyParty = true,
                            onClick = onClick,
                        )
                    }
                }
            }

//        // parties
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(20.dp),
//            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
//        ) {
//            item {
//                Text(
//                    text = "Hosted by Me",
//                    fontWeight = FontWeight.Bold,
//                    style = MaterialTheme.typography.headlineLarge,
//                    modifier = Modifier
//                        .padding(bottom = 5.dp, top = 10.dp)
//                )
//            }
//
//            if (hostingParties.isEmpty()) {
//                item {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .height(200.dp),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            } else {
//                items(hostingParties.size) {
//                    PartyItem(partyInfo = hostingParties[it], isMyParty = true, onClick = onClick)
//                }
//            }
//
//            item {
//                Text(
//                    text = "Upcoming Parties",
//                    fontWeight = FontWeight.Bold,
//                    style = MaterialTheme.typography.headlineLarge,
//                    modifier = Modifier
//                        .padding(bottom = 5.dp, top = 10.dp)
//                )
//            }
//            if (attendingParties.isEmpty()) {
//                item {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .height(200.dp),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            } else {
//                items(attendingParties.size) {
//                    PartyItem(
//                        partyInfo = attendingParties[it],
//                        isMyParty = true,
//                        onClick = onClick,
//                    )
//                }
//            }
//        }
    }
}