// To Do:
// 1. Hook up the search bar to the api and display parties
// returned by the api
// 2. Decide final values for the dropdown
// 3. Tags are specific to user and need to be fetched from
// the api
// 4. Entire page should be scrollable and not just the parties

package com.example.vibees.screens.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.vibees.screens.home.myparties.PartyItem
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.TextField
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.example.vibees.Api.APIInterface
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.GlobalAppState
import com.example.vibees.ui.theme.GrayWhite
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import com.example.vibees.Api.VibeesApi
import com.example.vibees.ui.theme.SubtleWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onClick: (id: String) -> Unit, modifier: Modifier
) {
    var userID by GlobalAppState::UserID
    var userName by GlobalAppState::UserName
    Column(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical = 25.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        var parties by remember {
            mutableStateOf(
                listOf<Party>()
            )
        }

        // fetch all parties from endpoint /parties
        val vibeesApi = VibeesApi()


        // Successful request
        val successfn: (List<Party>) -> Unit = { response ->
            Log.d("TAG", "success")
            parties = response
            Log.d("TAG", parties.toString())
        }

        // failed request
        val failurefn: (Throwable) -> Unit = { t ->
            Log.d("TAG", "FAILURE")
            Log.d("TAG", t.printStackTrace().toString())
        }

        val response = vibeesApi.getAllParties(successfn, failurefn)

        // Header
        Header(firstLine = "Welcome back", secondLine = userName)

        // Search bar
        var searchText by remember { mutableStateOf("") }
        var searchActive by remember { mutableStateOf(false) }

        SearchBar(
            query = searchText,
            onQueryChange = {
                searchText = it
            },
            onSearch = {
                searchActive = false
            },
            active = false,
            onActiveChange = {
                searchActive = it
            },
            colors = SearchBarDefaults.colors(containerColor = SubtleWhite),
            tonalElevation = 50.dp,
            placeholder = {
                Text(text = "Search for a party")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                if (searchActive and searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                    }, content = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Close Icon"
                        )
                    })
                } else if (searchActive) {
                    IconButton(onClick = {
                        searchActive = false
                    }, content = {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                    })
                }
            },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .padding(bottom = 20.dp, top = 20.dp)
                .shadow(10.dp, RoundedCornerShape(35.dp))
        ) {

        }

        // Recommended Header with dropdown
        val options = listOf("Proximity", "Price", "Date", "Day")
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 5.dp)
        ) {
            Text(
                text = "Recommended",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .width(140.dp)
                    .padding(bottom = 15.dp)
            ) {
                OutlinedTextField(
                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                    modifier = Modifier
                        .menuAnchor()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
//                    label = { Text("Sort") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Arrow Up",
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        focusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color.Black,
                        focusedPlaceholderColor = Color.Red,
                        unfocusedPlaceholderColor = Color.Red,
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(SubtleWhite)
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        // User Interest Tags
        var tagList = listOf("Anime", "EDM", "Board Games", "Fraternity", "FIFA")
        Tags(tagList = tagList)

        if (parties.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Parties
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(30.dp),
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
            ) {
                Log.d("TAG", parties.toString())
                items(parties.size) {
                    PartyItem(partyInfo = parties[it], isMyParty = false, onClick = onClick)
                }
            }
        }
    }
}