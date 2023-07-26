// Vibees/app/src/main/java/com/example/vibees/screens/SettingsScreen.kt:

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.User

@Composable
fun SettingsScreen(navController: NavHostController) {
    var api = VibeesApi()
    val currentUser = GlobalAppState.currentUser
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentUser != null) {
            UserDetailsSection(
                api = api,
                currentUser = currentUser,
                onUpdateUser = { updatedUser ->
                    GlobalAppState.currentUser = updatedUser
                }
            )
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
    val updateContext = LocalContext.current

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
                    Toast.makeText(updateContext, "Your personal details were updated!", Toast.LENGTH_LONG).show()
                },
                failurefn = { error ->
                    Toast.makeText(updateContext, "Could not update personal details", Toast.LENGTH_LONG).show()
                    // Handle failure response if needed
                },
                requestModel = user
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Manage your account",
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(text = "Update your personal details or delete your account",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(top = 5.dp, bottom = 15.dp)
        )

        Text(text = "Change your personal details by entering new information below and clicking 'Update'",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 5.dp)
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { androidx.compose.material3.Text("New First Name") },
            placeholder = { androidx.compose.material3.Text("First Name", color = Color.Gray) },
            enabled = true,
            modifier = Modifier
                .padding(2.dp)
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
            )
        )
//        TextField(
//            value = firstName,
//            onValueChange = { firstName = it },
//            label = { Text("New First Name") }
//        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { androidx.compose.material3.Text("New Last Name") },
            placeholder = { androidx.compose.material3.Text("Last Name", color = Color.Gray) },
            enabled = true,
            modifier = Modifier
                .padding(2.dp)
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
            )
        )
//        TextField(
//            value = lastName,
//            onValueChange = { lastName = it },
//            label = { Text("New Last Name") }
//        )

        OutlinedTextField(
            value = addressStreet,
            onValueChange = { addressStreet = it },
            label = { androidx.compose.material3.Text("New Address Street") },
            placeholder = { androidx.compose.material3.Text("Address Street ", color = Color.Gray) },
            enabled = true,
            modifier = Modifier
                .padding(2.dp)
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
            )
        )

        OutlinedTextField(
            value = addressCity,
            onValueChange = { addressCity = it },
            label = { androidx.compose.material3.Text("New Address City") },
            placeholder = { androidx.compose.material3.Text("Address City", color = Color.Gray) },
            enabled = true,
            modifier = Modifier
                .padding(2.dp)
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
            )
        )

        OutlinedTextField(
            value = addressProv,
            onValueChange = { addressProv = it },
            label = { androidx.compose.material3.Text("New Province") },
            placeholder = { androidx.compose.material3.Text("Province", color = Color.Gray) },
            enabled = true,
            modifier = Modifier
                .padding(2.dp)
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
            )
        )

        OutlinedTextField(
            value = addressPostal,
            onValueChange = { addressPostal = it },
            label = { androidx.compose.material3.Text("New Postal Code") },
            placeholder = { androidx.compose.material3.Text("Postal Code", color = Color.Gray) },
            enabled = true,
            modifier = Modifier
                .padding(2.dp)
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,//Show next as IME button
                keyboardType = KeyboardType.Text, //Plain text keyboard
            )
        )

//        TextField(
//            value = addressStreet,
//            onValueChange = { addressStreet = it },
//            label = { Text("New Address Street") }
//        )
//        TextField(
//            value = addressCity,
//            onValueChange = { addressCity = it },
//            label = { Text("New Address City") }
//        )
//        TextField(
//            value = addressProv,
//            onValueChange = { addressProv = it },
//            label = { Text("New Address Province") }
//        )
//        TextField(
//            value = addressPostal,
//            onValueChange = { addressPostal = it },
//            label = { Text("New Address Postal Code") }
//        )
        Button(
            onClick = onSaveClick,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 5.dp),
            colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Update")
        }

        Text(text = "Or delete your account by clicking the 'Delete Account' ",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 5.dp)
        )

    }
}

@Composable
private fun DeleteAccountSection(
    api: VibeesApi,
    currentUser: User,
    navController: NavHostController
) {
    val deleteContext = LocalContext.current
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
                    Toast.makeText(deleteContext, "Account successfully deleted. Restart the app.", Toast.LENGTH_LONG).show()
                },
                failurefn = { error ->
                    Toast.makeText(deleteContext, "Account could not be deleted", Toast.LENGTH_LONG).show()
                    // Handle failure response if needed
                }
            )
        }
    }
    Button(
        onClick = onDeleteAccountClick,
        colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Delete Account")
    }
}
