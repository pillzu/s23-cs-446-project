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
import com.example.vibees.screens.home.myparties.PartyItem

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.ui.graphics.Color
import com.example.vibees.Api.APIInterface
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.GlobalAppState
import com.example.vibees.ui.theme.GrayWhite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onClick: (id: String) -> Unit,
    modifier: Modifier
) {
    var userID by GlobalAppState::UserID
    var userName by GlobalAppState::UserName
    Column(
        modifier = modifier
            .padding(horizontal = 15.dp, vertical=25.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        var parties by remember { mutableStateOf(listOf<Party>(
            Party(
                user_id = "12345",
                name = "Summer Bash",
                date_time = LocalDateTime.now(),
                type = "Outdoor",
                max_cap = 100,
                entry_fee = 10.99,
                desc = "Join us for a fun summer party!",
                street = "123 Main St",
                city = "Exampleville",
                prov = "Exampleland",
                postal_code = "12345",
                party_id = "abc123",
                host_name = "John Doe",
                qr_endpoint = "https://example.com/qrcode"
            )
        )) }

        // fetch all parties from endpoint /parties
        val apiService = APIInterface()
        val callResponse = apiService.getAllParties()
        val response = callResponse.enqueue(
            object: Callback<List<Party>> {
                override fun onResponse(
                    call: Call<List<Party>>,
                    response: Response<List<Party>>
                ) {
                    Log.d("TAG", "success")
                    parties = response.body()!!
                    Log.d("TAG", parties.toString())
                }

                override fun onFailure(call: Call<List<Party>>, t: Throwable) {
                    Log.d("TAG", "FAILURE")
                    Log.d("TAG", t.printStackTrace().toString())
                }
            }
        )

        // Header
        Header(firstLine = "Welcome back", secondLine = userName)

        // Search bar
        var searchText by remember { mutableStateOf("")}
        var searchActive by remember { mutableStateOf(false)}

        SearchBar(
            query = searchText,
            onQueryChange = {
                searchText = it
            },
            onSearch = {
                searchActive = false

            },
            active = searchActive,
            onActiveChange = {
                searchActive = it
            },

            colors = SearchBarDefaults.colors(containerColor = GrayWhite),
            placeholder = {
                Text(text = "Search for a party")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            trailingIcon = {
                if (searchActive and searchText.isNotEmpty()) {
                    IconButton(onClick = {
                        searchText = ""
                    }, content = {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                    })
                } else if (searchActive) {
                    IconButton(onClick = {
                        searchActive = false
                    }, content = {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up Icon")
                    })
                }
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .padding(bottom = 5.dp)
        ) {

        }

        // Recommended Header with dropdown
        val options = listOf("Proximity", "Price", "Date", "Day")
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
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
                TextField(
                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    label = { Text("Sort") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        unfocusedContainerColor = GrayWhite,
                        focusedContainerColor = GrayWhite,
                        focusedLabelColor = Color.Black,
                ))
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
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