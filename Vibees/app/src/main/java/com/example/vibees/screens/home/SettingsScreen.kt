// Vibees/app/src/main/java/com/example/vibees/screens/SettingsScreen.kt:

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vibees.Api.VibeesApi
import com.example.vibees.Models.User
import com.example.vibees.GlobalAppState
import com.example.vibees.graphs.Graph

@Composable
fun SettingsScreen(navController: NavHostController) {
    var api = VibeesApi()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        UserDetailsSection(api = api)
        Spacer(modifier = Modifier.height(16.dp))
        DeleteAccountSection(api = api, navController = navController)
    }
}

@Composable
private fun UserDetailsSection(api: VibeesApi) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var addressStreet by remember { mutableStateOf(TextFieldValue()) }
    var addressCity by remember { mutableStateOf(TextFieldValue()) }
    var addressProv by remember { mutableStateOf(TextFieldValue()) }
    var addressPostal by remember { mutableStateOf(TextFieldValue()) }

    // Function to handle the save button click
    val onSaveClick: () -> Unit = {
        val currentUser = GlobalAppState.currentUser

        val updatedUser = currentUser?.copy(
            first_name = firstName.text,
            last_name = lastName.text,
            address_street = addressStreet.text,
            address_city = addressCity.text,
            address_prov = addressProv.text,
            address_postal = addressPostal.text
        )

        updatedUser?.let { user ->
            api.updateUserDetails(
                user_id = user.user_id.toString(),
                successfn = { response ->
                    // Handle success response if needed
                },
                failurefn = { error ->
                    // Handle failure response if needed
                },
                requestModel = user
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") }
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") }
        )
        TextField(
            value = addressStreet,
            onValueChange = { addressStreet = it },
            label = { Text("Address Street") }
        )
        TextField(
            value = addressCity,
            onValueChange = { addressCity = it },
            label = { Text("Address City") }
        )
        TextField(
            value = addressProv,
            onValueChange = { addressProv = it },
            label = { Text("Address Province") }
        )
        TextField(
            value = addressPostal,
            onValueChange = { addressPostal = it },
            label = { Text("Address Postal Code") }
        )
        Button(
            onClick = onSaveClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun DeleteAccountSection(api: VibeesApi, navController: NavHostController) {
    // Function to handle the delete account button click
    val onDeleteAccountClick: () -> Unit = {
        val currentUser = GlobalAppState.currentUser

        // Check if the current user exists before calling the API
        currentUser?.let { user ->
            api.deleteUserAccount(
                user_id = user.user_id.toString(),
                successfn = { response ->
                    // Handle success response if needed
                    // After successful deletion, navigate to login/signup screen
                    navController.popBackStack()
                    navController.navigate(Graph.AUTHENTICATION)
                },
                failurefn = { error ->
                    // Handle failure response if needed
                }
            )
        }
    }

    Button(
        onClick = onDeleteAccountClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Delete Account")
    }
}
