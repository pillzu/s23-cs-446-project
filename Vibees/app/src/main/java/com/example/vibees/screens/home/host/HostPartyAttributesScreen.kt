package com.example.vibees.screens.home.host

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.example.vibees.Api.APIInterface
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.R
import com.example.vibees.utils.URIPathHelper
import com.example.vibees.utils.toFileUri
import com.example.vibees.utils.uploadToCloudinary
import okhttp3.ResponseBody

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HostPartyAttributesScreen(
    onClick: () -> Unit
) {
    val selectedlist by GlobalAppState::TagList
    var selectedcount by remember { mutableIntStateOf(0) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imgUri = stringResource(R.string.default_avatar)

    val apiService = APIInterface()
    var userID by GlobalAppState::UserID
    var userName by GlobalAppState::UserName
    val vibeesApi = VibeesApi()
    val partyContext = LocalContext.current

    val partystore by GlobalAppState::PartyStore

    val photopickerlauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("HostScreen","PERMISSION GRANTED")

        } else {
            Log.d("HostScreen","PERMISSION DENIED")
        }
    }

    val tags = listOf("Dance", "Board game", "Karaoke", "Barbecue", "Pool", "Disco", "Birthday",
        "Graduation", "Adult only", "Business", "Formal", "Wedding", "Sports", "Bar Hopping", "Day",
        "Late Night")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                    .padding(20.dp),
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
                        onAdd = {
                            selectedlist.add(element)
                            selectedcount += 1
                                },
                        onRemove = {
                            selectedlist.remove(element)
                            selectedcount -= 1
                        }
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

            if (selectedImageUri == null) {
                Box(
                    Modifier
                        .size(250.dp, 80.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = Color.Gray,
                                style = Stroke(
                                    width = 2f,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(10f, 10f),
                                        0f
                                    )
                                ),
                                cornerRadius = CornerRadius(8.dp.toPx())
                            )
                        }
                        .clickable {
                            photopickerlauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
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
            } else {


                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        if (partystore?.isedit == true) {
            TextButton(
                onClick = {
                    partystore?.taglist = selectedlist
//                    partystore?.image = selectedImageUri.toString()

                    Log.d("STORE update", partystore.toString())

                    Log.d("UPDATE", imgUri)
                    // set default image here
//                    var imgUri = "https://res.cloudinary.com/dw9xmrzlz/image/upload/v1690315612/mramxypdh3edcplc0nux.jpg"
//                    if (partystore?.image!! != "") {
//                        var publicId = "${partystore?.name}-${userName}"
//                        uriToCloudinary(partyContext, selectedImageUri, publicId, launcher)
//                        imgUri = MediaManager.get().url().generate(publicId)
//                    }

                    val successfn: (ResponseMessage) -> Unit = { response ->
                        Log.d("TAG", response.message)
                        Toast.makeText(partyContext, response.message, Toast.LENGTH_LONG).show()
                        selectedlist.clear()
                        onClick()
                    }

                    // failed request
                    val failurefn: (ResponseBody) -> Unit = { response ->
                        Log.d("TAG", "FAILURE: could not update party.")
                        Log.d("TAG", response.toString())
                        Toast.makeText(partyContext, "ERROR: Could not update party.", Toast.LENGTH_LONG).show()
                    }

                    val obj = Party(partystore?.party_id, partystore?.image.toString(), partystore?.name, partystore?.date_time!!,
                        userID, partystore?.max_cap!!, partystore?.desc!!, partystore?.entry_fee!!.toDouble(),
                        partystore?.type, partystore?.drug!!, partystore?.byob!!, userName, partystore?.qr_endpoint,
                        partystore?.street!!, partystore?.city!!, partystore?.prov!!, partystore?.postal_code!!,
                        partystore?.taglist!!,  partystore?.attend_count!!)

                    Log.d("Obj value", obj.toString())

                    // call endpoint to update a party
                    vibeesApi.update_party(successfn, failurefn, partystore?.party_id!!, obj)
                },
                modifier = Modifier.align(Alignment.End),
                enabled = (selectedcount > 0) and (selectedcount < 6)
            ) {
                Text(
                    "Update",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = if ((selectedcount > 0) and (selectedcount < 6)) Color.Blue
                    else Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Arrow right"
                )
            }
        } else {
            TextButton(
                onClick = {
                    partystore?.taglist = selectedlist

                    Log.d("STORE", partystore.toString())

                    // set default image here
                    if (selectedImageUri != null){
                        var publicId = "${partystore?.name}-${userName}"
                        uriToCloudinary(partyContext, selectedImageUri, publicId, launcher)
                        imgUri = MediaManager.get().url().generate(publicId)
                    }

                    val successfn: (ResponseMessage) -> Unit = { response ->
                        Log.d("TAG", response.message)
                        Toast.makeText(partyContext, "Playlist successfully created!", Toast.LENGTH_LONG).show()
                        Toast.makeText(partyContext, response.message, Toast.LENGTH_LONG).show()
                        selectedlist.clear()
                        onClick()
                    }


                    // failed request
                    val failurefn: (ResponseBody) -> Unit = { response ->
                        Log.d("TAG", "FAILURE: could not create party.")
                        Log.d("TAG", response.toString())
                        Toast.makeText(partyContext, "ERROR: Could not create party.", Toast.LENGTH_LONG).show()
                    }

                    val obj = Party("", imgUri, partystore?.name, partystore?.date_time!!,
                        userID, partystore?.max_cap!!, partystore?.desc!!, partystore?.entry_fee!!.toDouble(),
                        partystore?.type, partystore?.drug!!, partystore?.byob!!, userName, "",
                        partystore?.street!!, partystore?.city!!, partystore?.prov!!, partystore?.postal_code!!,
                        partystore?.taglist!!, partystore?.attend_count!!)

                    Log.d("Obj value", obj.toString())

                    // call endpoint /parties/host to create a party
                    vibeesApi.createParty(successfn, failurefn, obj)
                },
                modifier = Modifier.align(Alignment.End),
                enabled = (selectedcount > 0) and (selectedcount < 6)
            ) {
                Text(
                    "Create",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = if ((selectedcount > 0) and (selectedcount < 6)) Color.Blue
                    else Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Arrow right"
                )
            }
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipTag(
    name: String,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    var selected by remember { mutableStateOf(false) }
    FilterChip(
        selected = selected,
        onClick = {
                    selected = !selected
                    if (selected) onAdd() else onRemove()
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

fun uriToCloudinary(partyContext: Context, selectedImageUri:Uri?, publicId: String , launcher: ManagedActivityResultLauncher<String, Boolean>): String {
    val uriPathHelper = URIPathHelper()
    val filePath = uriPathHelper.getPath(partyContext, selectedImageUri!!)
    Log.i("FilePath", filePath.toString())

    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            partyContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) -> {
            // Some works that require permission
            Log.d("HostScreen","Code has write permission")
        }
        else -> {
            // Asking for permission
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            partyContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) -> {
            // Some works that require permission
            Log.d("HostScreen","Code has read permission")
        }
        else -> {
            // Asking for permission
            launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // upload to cloudinary
    val imgUri = filePath?.let { uploadToCloudinary(it, publicId) }
    return imgUri!!
}