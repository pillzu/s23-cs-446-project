// Vibees/app/src/main/java/com/example/vibees/screens/SettingsScreen.kt:

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vibees.Api.VibeesApi
import com.example.vibees.Models.User
import com.example.vibees.GlobalAppState
import com.example.vibees.graphs.AuthScreen
import com.example.vibees.graphs.Graph
import com.example.vibees.graphs.authNavGraph
import com.example.vibees.screens.home.HomeScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SettingsScreen(navController: NavHostController) {
    var api = VibeesApi()
    val currentUser = GlobalAppState.currentUser
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentUser != null) {
            UserDetailsSection(
                api = api,
                currentUser = currentUser,
                onUpdateUser = { updatedUser ->
                    GlobalAppState.currentUser = updatedUser
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DeleteAccountSection(
                api = api,
                currentUser = currentUser,
                navController = navController

            )
        }
    }
}

@Composable
private fun UserDetailsSection(
    api: VibeesApi,
    currentUser: User,
    onUpdateUser: (User) -> Unit
) {
    var firstName by remember { mutableStateOf(TextFieldValue()) }
    var lastName by remember { mutableStateOf(TextFieldValue()) }
    var addressStreet by remember { mutableStateOf(TextFieldValue()) }
    var addressCity by remember { mutableStateOf(TextFieldValue()) }
    var addressProv by remember { mutableStateOf(TextFieldValue()) }
    var addressPostal by remember { mutableStateOf(TextFieldValue()) }

    // Function to handle the save button click
    val onSaveClick: () -> Unit = {

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
                    onUpdateUser(user)
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
            label = { Text("New First Name") }
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("New Last Name") }
        )
        TextField(
            value = addressStreet,
            onValueChange = { addressStreet = it },
            label = { Text("New Address Street") }
        )
        TextField(
            value = addressCity,
            onValueChange = { addressCity = it },
            label = { Text("New Address City") }
        )
        TextField(
            value = addressProv,
            onValueChange = { addressProv = it },
            label = { Text("New Address Province") }
        )
        TextField(
            value = addressPostal,
            onValueChange = { addressPostal = it },
            label = { Text("New Address Postal Code") }
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
private fun DeleteAccountSection(
    api: VibeesApi,
    currentUser: User,
    navController: NavHostController
) {
    // Function to handle the delete account button click
    val onDeleteAccountClick: () -> Unit = {
        // Check if the current user exists before calling the API
        currentUser?.let { user ->
            api.deleteUserAccount(
                user_id = user.user_id.toString(),
                successfn = { response ->
                    // Handle success response if needed
                    // After successful deletion, null current user and navigate to login/signup screen
                    GlobalAppState.currentUser = null

                    // TODO: Navigate to login/signup screen
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
