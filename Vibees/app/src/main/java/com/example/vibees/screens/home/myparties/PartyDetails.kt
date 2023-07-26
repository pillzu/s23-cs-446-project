package com.example.vibees.screens.home.myparties

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.example.vibees.Api.APIInterface
import com.example.vibees.Api.VibeesApi
import com.example.vibees.GlobalAppState
import com.example.vibees.Models.Party
import com.example.vibees.Models.ResponseMessage
import com.example.vibees.graphs.HostScreens
import com.example.vibees.qr_scanner.PreviewViewComposable
import com.example.vibees.screens.bottombar.BottomBar
import com.example.vibees.screens.home.host.PartyStore
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun checkAndRequestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
        // Request a permission
        SideEffect {
            launcher.launch(permission)
        }
    }
}
@androidx.camera.core.ExperimentalGetImage
@Composable
fun PartyDetails(
    navController: NavHostController,
    id: String,
) {
    var userID by GlobalAppState::UserID
    val apiService = APIInterface()
    var partyDetails by GlobalAppState::PartyDetails
    var partystore by GlobalAppState::PartyStore
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val vibeesApi = VibeesApi()

    val openDeleteDialog = remember { mutableStateOf(false) }
    val openLeaveDialog = remember { mutableStateOf(false) }

    val successfn_unattend: (ResponseMessage) -> Unit = { response ->
        Log.d("TAG UNATTEND", "success: unattended party")
    }

    val failurefn_unattend: (Throwable) -> Unit = { t ->
        Log.d("TAG UNATTEND", "FAILURE: unattend party")
        Log.d("TAG UNATTEND", t.printStackTrace().toString())
    }

    val successfn_cancel_party: (ResponseMessage) -> Unit = { response ->
        Log.d("TAG CANCEL PARTY", "success: party cancelled")
    }

    val failurefn_cancel_party: (Throwable) -> Unit = { t ->
        Log.d("TAG CANCEL PARTY", "FAILURE: party cancelling")
        Log.d("TAG CANCEL PARTY", t.printStackTrace().toString())
    }

    val successfn_update_party: (ResponseMessage) -> Unit = { response ->
        Log.d("TAG UPDATE PARTY", "success: party updated")
    }

    val failurefn_update_party: (Throwable) -> Unit = { t ->
        Log.d("TAG UPDATE PARTY", "FAILURE: party updating")
        Log.d("TAG UPDATE PARTY", t.printStackTrace().toString())
    }


    if (openDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDeleteDialog.value = false
            },
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color.Red) },
            title = {
                Text(text = "Confirm Delete", color = Color.Black,
                    fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
            },
            text = {
                Text(
                    "Are you sure you want to delete this party? " +
                            " This action is irreversible!.",
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog.value = false

                        // delete party
                        vibeesApi.cancelParty(successfn_cancel_party, failurefn_cancel_party, partyDetails?.party_id!!)

                        navController.navigate(BottomBar.MyParties.route) {
                            popUpTo(BottomBar.MyParties.route) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text("Confirm", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDeleteDialog.value = false
                    }
                ) {
                    Text("Dismiss", color = Color.Black)
                }
            },
        )
    }

    if (openLeaveDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openLeaveDialog.value = false
            },
            icon = { Icon(Icons.Filled.Warning, contentDescription = "Warning", tint = Color.Red) },
            title = {
                Text(text = "Confirm Leave", color = Color.Black,
                    fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleLarge.fontSize)
            },
            text = {
                Text(
                    "Are you sure you want to leave this party? " +
                            " You will need to register again if you change your mind later.",
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openLeaveDialog.value = false

                        // leave party
                        vibeesApi.unattendUserFromParty(successfn_unattend, failurefn_unattend, partyDetails?.party_id!!, userID.toString())

                        navController.navigate(BottomBar.MyParties.route) {
                            popUpTo(BottomBar.MyParties.route) {
                                inclusive = true
                            }
                        }
                    }
                ) {
                    Text("Confirm", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openLeaveDialog.value = false
                    }
                ) {
                    Text("Dismiss", color = Color.Black)
                }
            },
        )
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
                    .size(40.dp)
                    .clickable {
                        clipboardManager.setText(AnnotatedString("https://vibees.ca/party/${id}"))
                        Toast
                            .makeText(
                                context,
                                "Invite link copied to clipboard!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
            if (partyDetails?.host_id == userID) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .clickable {
                            // populate fields
                            partystore = PartyStore(
                                partyDetails?.street,
                                partyDetails?.city,
                                partyDetails?.prov,
                                partyDetails?.postal_code,
                                partyDetails?.date_time,
                                partyDetails?.name,
                                partyDetails?.type,
                                partyDetails?.entry_fee?.roundToInt(),
                                partyDetails?.desc,
                                partyDetails?.drug,
                                partyDetails?.byob,
                                partyDetails?.tags,
                                partyDetails?.party_avatar_url,
                                partyDetails?.host_id,
                                partyDetails?.max_cap,
                                partyDetails?.party_id,
                                partyDetails?.host_name,
                                partyDetails?.qr_endpoint,
                                isedit = true,
                                partyDetails?.attend_count
                            )
                            navController.navigate(HostScreens.Step1.route)
                        }
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .clickable {
                            // show modal
                            openDeleteDialog.value = true

                        }
                )
            } else {
                Icon(
                    imageVector = Leave(),
                    contentDescription = "Share",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .clickable {
                            // show modal
                            openLeaveDialog.value = true
                        }
                )
            }

        }
        // QR Code section
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(top = 50.dp)
        ) {
            if (partyDetails?.host_id != userID) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary)
                        .clip(RoundedCornerShape(15.dp))
                ) {

                    Barcode(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 20.dp, vertical = 15.dp)
                            .width(250.dp)
                            .height(250.dp),
                        resolutionFactor = 10, // Optionally, increase the resolution of the generated image
                        type = BarcodeType.QR_CODE, // pick the type of barcode you want to render
                        value = "${partyDetails?.qr_endpoint}" // The textual representation of this code
                    )
                    Text(
                        text = partyDetails?.name!!,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(15.dp)
                    )
                }
            } else {
                val context = LocalContext.current

                val permission = Manifest.permission.CAMERA
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        Toast.makeText(
                            context,
                            "Failed to initialize camera...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                checkAndRequestCameraPermission(context, permission, launcher)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                ) {
                    PreviewViewComposable(navController, partyDetails?.party_id!!)
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Party Icon",
                modifier = Modifier
                    .size(100.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            androidx.compose.material3.Text(
                text = partyDetails?.name!!,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Hosted by ${partyDetails?.host_name}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Date",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        color = Color.Black, text = partyDetails?.date_time!!.format(
                            DateTimeFormatter.ISO_DATE
                        )
                    )
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Location",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = partyDetails?.street!!)
                    Text(
                        color = Color.Black,
                        text = "${partyDetails?.city}, ${partyDetails?.prov}"
                    )
                    Text(color = Color.Black, text = partyDetails?.postal_code!!)
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Maximum Capacity",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(color = Color.Black, text = partyDetails?.max_cap.toString())
                }
            }

            Column {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Type",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = "EDM")
                    Text(color = Color.Black, text = "Alcohol-free")
                    Text("")
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Entry Fees",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(color = Color.Black, text = partyDetails?.entry_fee.toString())
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Description",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(color = Color.Black, text = partyDetails?.desc!!)
            }
        }
    }
}

@Composable
fun Leave(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "leave",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16.375f, 26.958f)
                quadToRelative(-0.375f, -0.416f, -0.375f, -1f)
                quadToRelative(0f, -0.583f, 0.375f, -0.958f)
                lineToRelative(3.667f, -3.708f)
                horizontalLineToRelative(-13.5f)
                quadToRelative(-0.584f, 0f, -0.959f, -0.375f)
                reflectiveQuadTo(5.208f, 20f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.395f, 0.959f, -0.395f)
                horizontalLineToRelative(13.5f)
                lineToRelative(-3.709f, -3.709f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.937f)
                quadToRelative(0f, -0.563f, 0.417f, -0.979f)
                quadToRelative(0.375f, -0.417f, 0.958f, -0.417f)
                quadToRelative(0.584f, 0f, 0.959f, 0.375f)
                lineToRelative(6.041f, 6.083f)
                quadToRelative(0.209f, 0.209f, 0.313f, 0.438f)
                quadToRelative(0.104f, 0.229f, 0.104f, 0.479f)
                quadToRelative(0f, 0.292f, -0.104f, 0.5f)
                quadToRelative(-0.104f, 0.208f, -0.313f, 0.417f)
                lineTo(18.292f, 27f)
                quadToRelative(-0.417f, 0.417f, -0.959f, 0.396f)
                quadToRelative(-0.541f, -0.021f, -0.958f, -0.438f)
                close()
                moveTo(7.833f, 34.75f)
                quadToRelative(-1.041f, 0f, -1.833f, -0.792f)
                quadToRelative(-0.792f, -0.791f, -0.792f, -1.875f)
                verticalLineToRelative(-6.791f)
                quadToRelative(0f, -0.584f, 0.375f, -0.959f)
                reflectiveQuadToRelative(0.959f, -0.375f)
                quadToRelative(0.541f, 0f, 0.916f, 0.375f)
                reflectiveQuadToRelative(0.375f, 0.959f)
                verticalLineToRelative(6.791f)
                horizontalLineToRelative(24.292f)
                verticalLineTo(7.833f)
                horizontalLineTo(7.833f)
                verticalLineToRelative(6.875f)
                quadToRelative(0f, 0.584f, -0.375f, 0.959f)
                reflectiveQuadToRelative(-0.916f, 0.375f)
                quadToRelative(-0.584f, 0f, -0.959f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.959f)
                verticalLineTo(7.833f)
                quadToRelative(0f, -1.083f, 0.792f, -1.854f)
                quadToRelative(0.792f, -0.771f, 1.833f, -0.771f)
                horizontalLineToRelative(24.292f)
                quadToRelative(1.083f, 0f, 1.875f, 0.771f)
                reflectiveQuadToRelative(0.792f, 1.854f)
                verticalLineToRelative(24.25f)
                quadToRelative(0f, 1.084f, -0.792f, 1.875f)
                quadToRelative(-0.792f, 0.792f, -1.875f, 0.792f)
                close()
            }
        }.build()
    }
}